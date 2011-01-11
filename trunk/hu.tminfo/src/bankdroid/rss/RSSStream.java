package bankdroid.rss;

import hu.tminfo.Codes;
import hu.tminfo.R;
import hu.tminfo.RSSItemProvider;
import hu.tminfo.widget.PortalWidgetProvider;
import hu.tminfo.widget.Widget11Provider;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Gabe
 */
public class RSSStream implements Codes
{

	private static final int HTTP_TIMEOUT = 20000;
	private static final String DEFAULT_EXTRACTOR = "__DEFAULT__";
	private static final Map<String, IDExtractor> extractors = new HashMap<String, IDExtractor>();

	static
	{
		extractors.put(DEFAULT_EXTRACTOR, new EndIDExtractor());
	}

	private static IDExtractor getIDExtractor( final String channelTag )
	{
		if ( extractors.containsKey(channelTag) )
			return extractors.get(channelTag);

		return extractors.get(DEFAULT_EXTRACTOR);
	}

	private RSSStream()
	{
		// static class to avoid create object out of it
	}

	/**
	 * Read the RSS stream and converts the stream into data objects.
	 * 
	 * @param stream
	 * @param maxId Only those articles are return that has greater ID than the current maxId (only new items are returned)
	 * @return RSSChannel object that represents the list of articles in the RSS stream
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	private static RSSChannel readChannelContent( final String channelTag, final InputStream stream, final int maxId )
			throws ParserConfigurationException, SAXException, IOException
	{
		final RSSHandler handler = new RSSHandler(maxId, getIDExtractor(channelTag));

		final SAXParserFactory spf = SAXParserFactory.newInstance();
		final SAXParser sp = spf.newSAXParser();
		final XMLReader xr = sp.getXMLReader();
		xr.setContentHandler(handler);

		final InputSource is = new InputSource(stream);
		try
		{
			xr.parse(is);
		}
		catch ( final SAXException e )
		{
			if ( !e.getMessage().equals("Max is ready.") )
				throw e;
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch ( final Exception e2 )
			{
				Log.e(TAG, "Failed to close the RSS stream.", e2);
			}
		}

		//set channel flag
		final RSSChannel channel = handler.getChannel();
		channel.tag = channelTag;

		for ( final RSSItem item : channel.items )
		{
			item.channels.add(channelTag);
		}

		return channel;
	}

	private static void storeItems( final Context context, final List<RSSItem> items )
	{
		final int length = items.size();
		final ContentValues[] valuesList = new ContentValues[length];
		for ( int i = 0; i < length; i++ )
		{
			final RSSItem item = items.get(i);

			final ContentValues values = new ContentValues();

			values.put(RSSObject.F__ID, item.id);
			values.put(RSSObject.F_DESCRIPTION, item.description);
			values.put(RSSObject.F_SUMMARY, item.summary);
			values.put(RSSObject.F_LINK, item.link);
			values.put(RSSObject.F_TITLE, item.title);
			values.put(RSSItem.F_AUTHOR, item.author);
			values.put(RSSItem.F_STATUS, item.status);
			values.put(RSSItem.F_CHANNELS, item.getChannelsAsString());
			if ( item.publishDate != null )
				values.put(RSSItem.F_PUBDATE, Formatters.getTimstampFormat().format(item.publishDate));

			valuesList[i] = values;
			Log.d(TAG, "Item with following id is going to be inserted: " + item.id);
		}

		final int count = context.getContentResolver().bulkInsert(RSSItem.CONTENT_URI, valuesList);
		Log.d(TAG, "Item count: " + count);
	}
	private static final Map<Object, HttpRequestBase> activeRequests = new HashMap<Object, HttpRequestBase>();

	/**
	 * Synchronizes the RSS feed and the database. It also maintains the database by purging the old items.
	 * Only new items are downloaded and inserted into the database. Existing items are not updated.
	 * @param context
	 * @param filter Only those feeds will be updated, which are not listed here (if it is not null) 
	 * @param invoker This object will be used as reference to stop the communication if necessary.
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static boolean synchronize( final Context context, final int[] filter, final Object invoker )
			throws ParserConfigurationException, SAXException, IOException
	{
		boolean hasNew = false;

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		//calculate expiry for the items
		final int expiry = Integer.parseInt(preferences.getString(PREF_EXPIRY, DEFAULT_EXPIRY));
		final Calendar limitCal = Calendar.getInstance();
		limitCal.add(Calendar.DATE, -expiry);
		final Date limit = limitCal.getTime();

		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, HTTP_TIMEOUT);
		final HttpClient client = new DefaultHttpClient(httpParams);

		//find out which channel to synch
		final String[] tags = context.getResources().getStringArray(R.array.feedLabels);
		for ( int i = 0; i < tags.length; i++ )
		{
			//check filter
			if ( filter != null )
			{
				boolean itemFiltered = true;
				for ( int j = 0; j < filter.length; j++ )
				{
					if ( filter[j] == i )
					{
						itemFiltered = false;
						break;
					}
				}

				if ( itemFiltered )
					continue;
			}

			//update the feed
			final boolean isSet = preferences.getBoolean(PREF_FEED_PREFIX + i, false);
			if ( isSet )
			{
				final String urlString = context.getResources().getStringArray(R.array.feedValues)[i];

				//read database list to get the max ID
				final Cursor maxIdCursor = context.getContentResolver()
						.query(
								RSSItem.CONTENT_URI,
								new String[] { RSSItem.F__ID },
								"_id = (select max(_id) from " + RSSItemProvider.T_RSSITEM + " where "
										+ RSSItem.F_CHANNELS + " like '%" + RSSItem.CHANNEL_SEPARATOR + tags[i]
										+ RSSItem.CHANNEL_SEPARATOR + "%' )", null, null);

				int maxId = -1;
				if ( maxIdCursor.moveToFirst() )
					maxId = maxIdCursor.getInt(0);
				maxIdCursor.close();

				//open connection with httpclient
				final HttpGet get = new HttpGet(urlString);
				activeRequests.put(invoker, get);
				HttpResponse response = null;
				try
				{
					response = client.execute(get);
				}
				finally
				{
					activeRequests.remove(invoker);
				}

				final InputStream content = response.getEntity().getContent();

				//read RSS feed
				final RSSChannel channel = readChannelContent(tags[i], content, maxId);

				//remove old items
				for ( final Iterator<RSSItem> iterator = channel.items.iterator(); iterator.hasNext(); )
				{
					final RSSItem item = iterator.next();

					if ( item.publishDate.before(limit) )
						iterator.remove();
				}

				//insert new items into the DB
				if ( !channel.items.isEmpty() )
				{
					storeItems(context, channel.items);
					hasNew = true;
				}
			}
		}

		//delete old values
		final int delete = context.getContentResolver().delete(RSSItem.CONTENT_URI, RSSItem.F_PUBDATE + "<?",
				new String[] { Formatters.getTimstampFormat().format(limit) });
		Log.d(TAG, delete + " items deleted due expiry.");

		return hasNew;
	}

	public static void abortSynch( final Object invoker )
	{
		if ( activeRequests.containsKey(invoker) )
		{
			final HttpRequestBase request = activeRequests.get(invoker);
			request.abort();
			Log.d(TAG, "Request aborted: " + request);
		}
	}

	public static void setStatus( final Context context, final Uri item, final int status )
	{
		final ContentValues values = new ContentValues();
		values.put(RSSItem.F_STATUS, status);
		context.getContentResolver().update(item, values, null, null);

		// Push update for this widget to the home screen
		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		PortalWidgetProvider.updateWidgets(context, appWidgetManager);
		Widget11Provider.updateWidgets(context, appWidgetManager);

	}

	public static void markAllAsRead( final Context context )
	{
		final ContentValues values = new ContentValues();
		values.put(RSSItem.F_STATUS, RSSItem.STATUS_READ);
		context.getContentResolver().update(RSSItem.CONTENT_URI, values,
				RSSItem.F_STATUS + " = " + RSSItem.STATUS_UNREAD, null);
	}

	/**
	 * Reads the latest item from the database. This is primary for the widget 
	 * @param context
	 */
	public static RSSItem getLast( final Context context )
	{
		//read database list to get the max ID
		final Cursor lastCursor = context.getContentResolver()
				.query(
						RSSItem.CONTENT_URI,
						new String[] { RSSItem.F__ID, RSSItem.F_AUTHOR, RSSItem.F_PUBDATE, RSSItem.F_SUMMARY,
								RSSItem.F_TITLE },
						RSSItem.F_PUBDATE + " = (select max(" + RSSItem.F_PUBDATE + ") from "
								+ RSSItemProvider.T_RSSITEM + " )", null, null);

		RSSItem item = null;
		if ( lastCursor.moveToFirst() )
		{
			item = new RSSItem();
			item.id = lastCursor.getLong(0);
			item.author = lastCursor.getString(1);
			try
			{
				item.publishDate = Formatters.getTimstampFormat().parse(lastCursor.getString(2));
			}
			catch ( final ParseException e )
			{
				Log.e(TAG, "Failed to parse the publish date for item " + item.id, e);
			}
			item.summary = lastCursor.getString(3);
			item.title = lastCursor.getString(4);
		}
		lastCursor.close();
		return item;
	}

	/**
	 * Returns whether there is unread article in the database, in order to indicate this fact
	 * on the widget, 
	 * @param context
	 */
	public static boolean isThereUndread( final Context context )
	{
		return getUndreadCount(context) > 0;
	}

	/**
	 * Returns whether there is unread article in the database, in order to indicate this fact
	 * on the widget, 
	 * @param context
	 */
	public static int getUndreadCount( final Context context )
	{
		final Cursor lastCursor = context.getContentResolver().query(RSSItem.CONTENT_URI,
				new String[] { RSSItem.F__ID }, RSSItem.F_STATUS + "=" + RSSItem.STATUS_UNREAD, null, null);
		final int unreadCount = lastCursor.getCount();
		lastCursor.close();
		return unreadCount;
	}

	/**
	 * Cleans up the database.
	 * @param context
	 */
	public static void deleteItems( final Context context )
	{
		context.getContentResolver().delete(RSSItem.CONTENT_URI, null, null);
	}

	/**
	 * Deletes item related to the listed channels.
	 * @param context
	 * @param feedRemoved List of indexes of the channels (index is sequence of the Preference array)
	 */
	public static void deleteChannels( final Context context, final int[] feedRemoved )
	{
		final String[] tags = context.getResources().getStringArray(R.array.feedLabels);
		final String[] tagsRemoved = new String[feedRemoved.length];
		for ( int i = 0; i < tagsRemoved.length; i++ )
		{
			tagsRemoved[i] = tags[feedRemoved[i]];
		}

		context.getContentResolver().delete(RSSItem.CONTENT_URI, RSSItem.F_CHANNELS, tagsRemoved);
	}

}
