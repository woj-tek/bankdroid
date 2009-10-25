package bankdroid.soda;

import java.lang.reflect.Field;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;

public class BankDefinitionHandler extends DefaultHandler
{
	public final static String E_BANKS = "Banks";
	public final static String E_BANK = "Bank";
	public final static String E_NAME = "Name";
	public final static String E_ICONID = "IconId";
	public final static String E_EXPIRY = "Expiry";
	public final static String E_PHONES = "Phones";
	public final static String E_PHONE = "Phone";
	public final static String E_EXPRESSIONS = "Expressions";
	public final static String E_EXPRESSION = "Expression";

	public final static String A_PACKAGE = "package";
	public final static String A_COUNTRY = "county";

	private LinkedList<String> elements;
	private String country;
	private String packageName;
	private Class<?> drawable;
	private LinkedList<Bank> banks;

	/**
	 * Reset the handler. Call it before starting to parse any XML.
	 */
	public void reset()
	{
		if ( elements == null )
		{
			elements = new LinkedList<String>();
		}
		else
		{
			elements.clear();
		}

		country = null;
		packageName = null;

		if ( banks == null )
		{
			banks = new LinkedList<Bank>();
		}
		else
		{
			banks.clear();
		}

	}

	@Override
	public void startElement( final String uri, final String localName, final String qName, final Attributes attributes )
			throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);
		String name = localName;
		if ( TextUtils.isEmpty(name) )
			name = qName;
		elements.add(name);

		if ( name.equals(E_BANKS) )
		{
			//root tag. Look for country and package attributes
			country = attributes.getValue(A_PACKAGE);
			if ( country == null )
				throw new SAXException("Missing country attribute in the root tag.");

			packageName = attributes.getValue(A_PACKAGE);

			if ( packageName == null )
				throw new SAXException("Missing package attribute in the root tag.");

			try
			{
				final Class<?> R = Class.forName(packageName + ".R");
				final Class<?>[] declaredClasses = R.getDeclaredClasses();
				drawable = null;
				for ( final Class<?> class1 : declaredClasses )
				{
					if ( class1.getSimpleName().equals("drawable") )
						drawable = class1;
				}
				if ( drawable == null )
					throw new SAXException("Drawable is missing from the " + packageName + ".R class.");
			}
			catch ( final ClassNotFoundException e )
			{
				throw new SAXException("Invalid package name - R class is not available: " + packageName, e);
			}
		}
		else if ( name.equals(E_BANK) )
		{
			//create a new Bank
			final Bank bank = new Bank();
			banks.add(bank);
		}
	}

	@Override
	public void characters( final char[] ch, final int start, final int length ) throws SAXException
	{
		final String element = elements.getLast();
		final Bank bank = banks.size() > 0 ? banks.getLast() : null;
		if ( element.equals(E_EXPIRY) )
		{
			final int expiry = Integer.parseInt(new String(ch, start, length));
			bank.setExpiry(expiry);
		}
		else if ( element.equals(E_EXPRESSION) )
		{
			bank.addExtractExpression(new String(ch, start, length));
		}
		else if ( element.equals(E_ICONID) )
		{
			if ( drawable == null )
				throw new SAXException("Icon ID was referred, but no icon is available in " + packageName + "R class.");

			try
			{
				final Field iconIdField = drawable.getDeclaredField(new String(ch, start, length));
				final int iconId = iconIdField.getInt(null);

				bank.setIconId(iconId);
			}
			catch ( final Exception e )
			{
				throw new SAXException("Icon ID is not available in " + packageName + "R.drawable.");
			}
		}
		else if ( element.equals(E_NAME) )
		{
			bank.setName(new String(ch, start, length));
		}
		else if ( element.equals(E_PHONE) )
		{
			bank.addPhoneNumber(new String(ch, start, length));
		}
	}

	@Override
	public void endElement( final String uri, final String localName, final String qName ) throws SAXException
	{
		super.endElement(uri, localName, qName);
		String name = localName;
		if ( TextUtils.isEmpty(name) )
			name = qName;

		final String elementToClose = elements.removeLast();

		if ( !elementToClose.equals(name) )
			throw new SAXException("Invalid closing tag. " + elementToClose + "!=" + name);

	}

	public Bank[] getBanks()
	{
		return banks.toArray(new Bank[banks.size()]);
	}
}
