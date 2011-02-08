package bankdroid.soda.bank;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
	public final static String A_TRANSACTION_SIGN = "transactionSign";

	private static int idCounter = 0;

	private LinkedList<String> elements;
	private String country;
	private String packageName;
	private LinkedList<Bank> banks;

	private boolean transactionSignFlag = false;

	private final StringBuilder elementValue = new StringBuilder();

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
		if ( name == null || name.equals("") )
			name = qName;
		elements.add(name);

		if ( name.equals(E_BANKS) )
		{
			//root tag. Look for country and package attributes
			country = attributes.getValue(A_COUNTRY);
			if ( country == null )
				throw new SAXException("Missing country attribute in the root tag.");

			packageName = attributes.getValue(A_PACKAGE);

			if ( packageName == null )
				throw new SAXException("Missing package attribute in the root tag.");
		}
		else if ( name.equals(E_BANK) )
		{
			//create a new Bank
			final Bank bank = new Bank();
			bank.setCountryCode(country);
			bank.setId(idCounter++);
			banks.add(bank);
		}
		else if ( name.equals(E_EXPRESSION) )
		{
			final String transactionSign = attributes.getValue(A_TRANSACTION_SIGN);
			if ( transactionSign == null )
			{
				transactionSignFlag = false;
			}
			else
			{
				transactionSignFlag = Boolean.parseBoolean(transactionSign);
			}
		}
		elementValue.delete(0, elementValue.length());
	}

	@Override
	public void characters( final char[] ch, final int start, final int length ) throws SAXException
	{
		elementValue.append(ch, start, length);
	}

	private void processElementValue( final String element ) throws SAXException
	{
		final Bank bank = banks.size() > 0 ? banks.getLast() : null;
		if ( element.equals(E_EXPIRY) )
		{
			final int expiry = Integer.parseInt(elementValue.toString());
			bank.setExpiry(expiry);
		}
		else if ( element.equals(E_EXPRESSION) )
		{
			final String expression = elementValue.toString();
			bank.addExtractExpression(new Expression(transactionSignFlag, expression));
		}
		else if ( element.equals(E_ICONID) )
		{
			bank.setIconName(elementValue.toString());
		}
		else if ( element.equals(E_NAME) )
		{
			bank.setName(elementValue.toString());
		}
		else if ( element.equals(E_PHONE) )
		{
			bank.addPhoneNumber(elementValue.toString());
		}
	}

	@Override
	public void endElement( final String uri, final String localName, final String qName ) throws SAXException
	{
		super.endElement(uri, localName, qName);

		String name = localName;
		if ( name == null || name.equals("") )
			name = qName;

		processElementValue(name);

		final String elementToClose = elements.removeLast();

		if ( !elementToClose.equals(name) )
			throw new SAXException("Invalid closing tag. " + elementToClose + "!=" + name);

	}

	public Bank[] getBanks()
	{
		return banks.toArray(new Bank[banks.size()]);
	}
}
