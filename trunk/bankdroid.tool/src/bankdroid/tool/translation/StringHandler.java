package bankdroid.tool.translation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class StringHandler extends DefaultHandler
{
	private final Strings strings;
	private String key;
	private StringBuilder value;

	public StringHandler( final Strings strings )
	{
		this.strings = strings;
	}

	@Override
	public void startElement( final String uri, final String localName, final String name, final Attributes attributes )
			throws SAXException
	{
		final String key = attributes.getValue("name");
		if ( key != null && key.trim().length() > 0 )
		{
			this.key = key.trim();
			value = new StringBuilder();
		}
	}

	@Override
	public void characters( final char[] ch, final int start, final int length ) throws SAXException
	{
		if ( value != null )
		{
			value.append(ch, start, length);
		}
	}

	@Override
	public void endElement( final String uri, final String localName, final String name ) throws SAXException
	{
		if ( value != null )
		{
			strings.set(key, value.toString().trim());
			value = null;
		}
	}
}
