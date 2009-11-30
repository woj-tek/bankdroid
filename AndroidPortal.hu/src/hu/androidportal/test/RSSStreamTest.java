package hu.androidportal.test;

import hu.androidportal.rss.RSSChannel;
import hu.androidportal.rss.RSSHandler;
import hu.androidportal.rss.RSSObject;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class RSSStreamTest extends TestCase
{

	private RSSChannel readChannelContent( final InputStream stream, final int maxId ) throws Exception
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
		finally
		{
			try
			{
				stream.close();
			}
			catch ( final Exception e2 )
			{
				System.err.println("Failed to close the RSS stream.");
				e2.printStackTrace();
			}
		}

		return handler.getChannel();

	}

	public void testParser() throws Exception
	{
		RSSChannel channel = readChannelContent(new URL("http://feeds.feedburner.com/magyarandroidportal")
				.openConnection().getInputStream(), 170);
		channel = readChannelContent(new URL("http://feeds.feedburner.com/magyarandroidportal").openConnection()
				.getInputStream(), 170);
		channel = readChannelContent(new URL("http://feeds.feedburner.com/magyarandroidportal").openConnection()
				.getInputStream(), 170);
		assertNotNull(channel);

		System.out.println(channel.toString());

		assertTrue(channel.items.size() == 10);

		for ( final RSSObject item : channel.items )
		{
			System.out.println(item.toString());
		}
	}
}
