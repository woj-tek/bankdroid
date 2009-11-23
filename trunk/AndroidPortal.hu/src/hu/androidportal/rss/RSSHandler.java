package hu.androidportal.rss;

import java.text.ParseException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class RSSHandler extends DefaultHandler
{
	//XML element names
	private static final String GUID = "guid";
	private static final String LINK = "link";
	private static final String ITEM = "item";
	private static final String TITLE = "title";
	private static final String CHANNEL = "channel";
	private static final String DESCRIPTION = "description";
	private static final String LANGUAGE = "language";
	private static final String PUBLISH_DATE = "pubDate";
	private static final String AUTHOR = "creator";

	// Number of articles to download
	private static final int ITEM_LIMIT = 15; //XXX convert it to preference
	public static final String LIMIT_REACHED = "LIMIT_REACHED";

	// Feed and Article objects to use for temporary storage
	private RSSItem item = null;
	private RSSChannel channel = null;

	private final StringBuilder elementValue = new StringBuilder();

	private int maxId = -1;

	public RSSHandler( final int maxId )
	{
		this.maxId = maxId;
	}

	@Override
	public void startElement( final String uri, final String localName, final String qName, final Attributes atts )
	{
		String name = localName.trim();
		if ( name.equals("") )
			name = qName.trim();

		if ( name.trim().equals(ITEM) )
		{
			item = new RSSItem();
		}
		else if ( name.trim().equals(CHANNEL) )
		{
			channel = new RSSChannel();
		}

		elementValue.delete(0, elementValue.length());
	}

	@Override
	public void endElement( final String uri, final String localName, final String qName ) throws SAXException
	{
		String name = localName.trim();
		if ( name.equals("") )
			name = qName.trim();

		final RSSObject object = item != null ? item : channel;
		if ( object != null )
		{
			if ( name.equals(TITLE) )
			{
				object.title = elementValue.toString();
			}
			else if ( name.equals(LINK) )
			{
				object.link = elementValue.toString();
			}
			else if ( name.equals(DESCRIPTION) )
			{
				object.description = elementValue.toString();
			}
			else if ( name.equals(GUID) )
			{

				object.id = Long.parseLong(elementValue.substring(0, elementValue.indexOf(" ")));
			}
		}

		if ( item != null )
		{
			if ( name.equals(PUBLISH_DATE) )
			{
				try
				{
					item.publishDate = Formatters.getDefaultFormat().parse(elementValue.toString());
				}
				catch ( final ParseException e )
				{
					throw new SAXParseException("Failed to parse timestamp of RSS Item.", null, e);
				}
			}
			else if ( name.equals(AUTHOR) )
			{
				item.author = elementValue.toString();
			}
			else if ( name.equals(ITEM) )
			{
				//item ready
				if ( item.id <= maxId )
					throw new SAXException(LIMIT_REACHED);

				channel.items.add(item);
				item.generateSummary();
				item = null;

				if ( channel.items.size() >= ITEM_LIMIT )
				{
					throw new SAXException(LIMIT_REACHED);
				}
			}
		}
		else if ( channel != null )
		{
			if ( name.equals(LANGUAGE) )
			{
				channel.language = elementValue.toString();
			}
			else if ( name.equals(CHANNEL) )
			{
				//item ready, do nothing
			}
		}
	}

	@Override
	public void characters( final char[] ch, final int start, final int length )
	{
		elementValue.append(ch, start, length);
	}

	public RSSChannel getChannel()
	{
		return channel;
	}

}
