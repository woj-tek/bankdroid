package bankdroid.smskey.bank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class BankDescriptor
{
	private static Bank[] defaultBanks = null;

	public static Bank[] getDefaultBanks() throws ParserConfigurationException, SAXException, IOException
	{
		if ( defaultBanks == null )
		{

			final List<Bank> result = new ArrayList<Bank>();
			new Banks_AT().append(result);
			new Banks_BE().append(result);
			new Banks_CH().append(result);
			new Banks_CZ().append(result);
			new Banks_DE().append(result);
			new Banks_HK().append(result);
			new Banks_HU().append(result);
			new Banks_IN().append(result);
			new Banks_MT().append(result);
			new Banks_MY().append(result);
			new Banks_NL().append(result);
			new Banks_NO().append(result);
			new Banks_PL().append(result);
			new Banks_RU().append(result);
			new Banks_SA().append(result);
			new Banks_SE().append(result);
			new Banks_SG().append(result);
			new Banks_SK().append(result);
			new Banks_TR().append(result);
			new Banks_UA().append(result);
			//new Banks_US().append(result);
			new Banks_VN().append(result);

			defaultBanks = result.toArray(new Bank[result.size()]);
		}
		return defaultBanks;
	}

	public static Bank[] findByPhoneNumber( String phoneNumber ) throws ParserConfigurationException, SAXException,
			IOException
	{
		phoneNumber = phoneNumber.trim();
		final Bank[] banks = getDefaultBanks();
		final List<Bank> bankFound = new ArrayList<Bank>();
		for ( final Bank bank : banks )
		{
			if ( bank.isBankPhoneNumber(phoneNumber) )
			{
				bankFound.add(bank);
			}
		}
		return bankFound.toArray(new Bank[bankFound.size()]);
	}

}
