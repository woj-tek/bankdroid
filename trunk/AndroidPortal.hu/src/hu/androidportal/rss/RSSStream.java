package hu.androidportal.rss;

import hu.androidportal.Codes;
import hu.androidportal.RSSItemProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * FIXME create service to read the stream
 * FIXME create widget that shows the last item
 * 
 * http://feeds.feedburner.com/magyarandroidportalblogok
 * http://feeds.feedburner.com/magyarandroidportal
 * @author Gabe
 *
 */
public class RSSStream implements Codes
{
	/**
	 * Number of days that the items should be stored.
	 */
	public final static int MAX_ITEMS_TO_STORE = 3;//FIXME add to preferences

	private RSSStream()
	{
		// static class to avoid create object out of it
	}

	/**
	 * @return
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	public static RSSChannel readChannelContent( final URL url ) throws ParserConfigurationException, SAXException,
			IOException
	{
		return readChannelContent(url.openStream(), -1);
	}

	private static RSSChannel readChannelContent( final URL url, final int maxId ) throws ParserConfigurationException,
			SAXException, IOException
	{
		return readChannelContent(url.openStream(), maxId);
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
	public static RSSChannel readChannelContent( final InputStream stream, final int maxId )
			throws ParserConfigurationException, SAXException, IOException
	{
		final RSSHandler handler = new RSSHandler(maxId);

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
			if ( !e.getMessage().equals(RSSHandler.LIMIT_REACHED) )
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

		return handler.getChannel();
	}

	/**
	 * Reads the full content of the stream, and tries to insert each articles as new item into the DB.
	 * @param context
	 * @param url
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void readAndStoreContent( final Context context, final URL url ) throws ParserConfigurationException,
			SAXException, IOException
	{
		final RSSChannel channel = readChannelContent(url);

		for ( final RSSItem item : channel.items )
		{
			final ContentValues values = new ContentValues();

			values.put(RSSObject.F__ID, item.id);
			values.put(RSSObject.F_DESCRIPTION, item.description);
			values.put(RSSObject.F_SUMMARY, item.summary);
			values.put(RSSObject.F_LINK, item.link);
			values.put(RSSObject.F_TITLE, item.title);
			values.put(RSSItem.F_AUTHOR, item.author);
			if ( item.publishDate != null )
				values.put(RSSItem.F_PUBDATE, Formatters.getTimstampFormat().format(item.publishDate));

			context.getContentResolver().insert(RSSItem.CONTENT_URI, values);
			Log.d(TAG, "Item with following id is inserted: " + item.id);
		}
	}

	/**
	 * Synchronizes the RSS feed and the database. It also maintains the database by purging the old items.
	 * Only new items are downloaded and inserted into the database. Existing items are not updated.
	 * @param context
	 * @param url
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static boolean synchronize( final Context context, final URL url ) throws ParserConfigurationException,
			SAXException, IOException
	{
		//read database list to get the max ID
		final Cursor maxIdCursor = context.getContentResolver().query(RSSItem.CONTENT_URI,
				new String[] { RSSItem.F__ID }, "_id = (select max(_id) from " + RSSItemProvider.T_RSSITEM + " )",
				null, null);
		maxIdCursor.moveToFirst();
		final int maxId = maxIdCursor.getInt(0);//FIXME fails if there is no record in database.

		//read RSS feed
		final RSSChannel channel = readChannelContent(url, maxId);

		//insert new items into the DB
		for ( final RSSItem item : channel.items )
		{
			final ContentValues values = new ContentValues();

			values.put(RSSObject.F__ID, item.id);
			values.put(RSSObject.F_DESCRIPTION, item.description);
			values.put(RSSObject.F_SUMMARY, item.summary);
			values.put(RSSObject.F_LINK, item.link);
			values.put(RSSObject.F_TITLE, item.title);
			values.put(RSSItem.F_AUTHOR, item.author);
			if ( item.publishDate != null )
				values.put(RSSItem.F_PUBDATE, Formatters.getTimstampFormat().format(item.publishDate));

			context.getContentResolver().insert(RSSItem.CONTENT_URI, values);
			Log.d(TAG, "Item with following id is inserted: " + item.id);
		}

		//delete old values
		final Calendar limit = Calendar.getInstance();
		limit.roll(Calendar.DATE, -MAX_ITEMS_TO_STORE);

		final int delete = context.getContentResolver().delete(RSSItem.CONTENT_URI, RSSItem.F_PUBDATE + "<?",
				new String[] { Formatters.getTimstampFormat().format(limit.getTime()) });
		Log.d(TAG, delete + " items deleted due expiry.");
		return !channel.items.isEmpty();
	}

	/**
	 * Deletes the latest item from the database. This is only for testing purposes (if last item is deleted, 
	 * then there is a job for synchronize()).
	 * @param context
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void deleteLast( final Context context ) throws ParserConfigurationException, SAXException,
			IOException
	{
		//read database list to get the max ID
		final Cursor maxIdCursor = context.getContentResolver().query(RSSItem.CONTENT_URI,
				new String[] { RSSItem.F__ID }, "_id = (select max(_id) from " + RSSItemProvider.T_RSSITEM + " )",
				null, null);
		maxIdCursor.moveToFirst();
		final int maxId = maxIdCursor.getInt(0);

		context.getContentResolver().delete(RSSItem.CONTENT_URI, RSSItem.F__ID + "=?",
				new String[] { String.valueOf(maxId) });

	}

	/**
	 * Cleans up the database.
	 * @param context
	 */
	public static void deleteItems( final Context context )
	{
		context.getContentResolver().delete(RSSItem.CONTENT_URI, null, null);
	}
}
