package bankdroid.soda.bank;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class BankDescriptor
{

	private static Bank[] defaultBanks = null;

	public static Bank[] getDefaultBanks() throws ParserConfigurationException, SAXException, IOException
	{
		if ( defaultBanks == null )
		{
			SAXParser parser = null;
			BankDefinitionHandler handler = null;

			final List<Bank> result = new ArrayList<Bank>();

			int index = 0;
			while ( true )
			{
				index++;

				final String fileName = "xml/bankdef" + index + ".xml";
				InputStream open = null;
				try
				{
					open = BankDescriptor.class.getResourceAsStream(fileName);
					if ( open == null )
						throw new NullPointerException("Input stream is null for " + fileName);
				}
				catch ( final Exception e )
				{
					System.out.println("No more bankdef is available: " + e);
					break;
				}

				if ( parser == null )
				{//lazy initialization
					final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
					parser = parserFactory.newSAXParser();
					handler = new BankDefinitionHandler();
				}
				handler.reset();
				parser.parse(open, handler);

				final Bank[] parsed = handler.getBanks();

				for ( final Bank bank : parsed )
				{
					System.out.println("Bank found: " + bank.getName());
					result.add(bank);
				}
			}

			Collections.sort(result, new Comparator<Bank>()
			{

				@Override
				public int compare( final Bank object1, final Bank object2 )
				{
					return object1.getName().compareTo(object2.getName());
				}
			});

			defaultBanks = result.toArray(new Bank[result.size()]);
		}
		return defaultBanks;
	}

	public static Bank findByPhoneNumber( final String phoneNumber ) throws ParserConfigurationException, SAXException,
			IOException
	{
		final Bank[] banks = getDefaultBanks();
		for ( final Bank bank : banks )
		{
			if ( bank.isBankPhoneNumber(phoneNumber) )
				return bank;
		}
		return null;
	}

	public static InputStream loadLogo( final Bank bank )
	{
		return BankDescriptor.class.getResourceAsStream("image/" + bank.getIconName() + ".png");
	}

}
