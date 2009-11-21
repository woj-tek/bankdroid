package hu.androidportal.rss;

import hu.androidportal.Codes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.ContentValues;
import android.content.Context;
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
		return readChannelContent(url.openStream());
	}

	/**
	 * @return
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	public static RSSChannel readChannelContent( final InputStream stream ) throws ParserConfigurationException,
			SAXException, IOException
	{
		final RSSHandler handler = new RSSHandler();

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

		return handler.getChannel();
	}

	/**
	 * FIXME get max ID
	 * FIXME extend handler with ID watch
	 * FIXME insert only new items
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

	public static void deleteItems( final Context context )
	{
		context.getContentResolver().delete(RSSItem.CONTENT_URI, null, null);
	}
}
