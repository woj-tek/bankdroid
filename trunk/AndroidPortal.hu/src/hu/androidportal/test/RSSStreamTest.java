package hu.androidportal.test;

import hu.androidportal.rss.RSSChannel;
import hu.androidportal.rss.RSSObject;
import hu.androidportal.rss.RSSStream;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

public class RSSStreamTest extends TestCase
{

	public void testParser() throws ParserConfigurationException, SAXException, IOException
	{
		final RSSChannel channel = RSSStream.readChannelContent(getClass().getResourceAsStream(
				"magyarandroidportal.xml"), -1);

		assertNotNull(channel);

		System.out.println(channel.toString());

		assertTrue(channel.items.size() == 10);

		for ( final RSSObject item : channel.items )
		{
			System.out.println(item.toString());
		}
	}
}
