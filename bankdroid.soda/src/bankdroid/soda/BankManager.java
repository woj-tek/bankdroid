package bankdroid.soda;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * TODO currently the bank list is hard coded but it should come from a configuration file.
 * TODO extensible default phone number list
 * @author gyenes
 *
 */
public final class BankManager implements Codes
{

	private BankManager()
	{
		// static class. Do not instantiate it...
	}

	private final static Bank[] banks = new Bank[] { //
			// +36309400700OTPdirekt - Belfoldi forint atutalas xxx szamlan yyy HUF osszeggel zzz szamlara. Azonosito: 90120437 
			new Bank(1, "OTP", 3600, new String[] { "+36309400700", "+36209400700" }, //
					new String[] { "OTPdirekt - [^:]*: ([0-9]*)" }, R.drawable.otp2_logo, "HU"),

			new Bank(2, "KHB", 1800, new String[] { "+36209000703" }, //
					new String[] { ".*K.H e-bank[^:]*: ([a-zA-Z0-9]{6}).*" }, R.drawable.kh_logo, "HU"),

			//+36707060660Az On Raiffeisen DirektNet egyszer hasznalatos jelszava: 76037367 Raiffeisen Bank Zrt.
			new Bank(3, "Raiffeisen Bank", 3600,
					new String[] { "+36707060660" }, //
					new String[] { ".* Raiffeisen DirektNet .* jelszava: ([0-9]*) .*" }, R.drawable.raiffeisen_logo,
					"HU"),

			// +36303444504Az on kezdeti SpectraNet bejelentkezesi jelszava: 2HWNVRNJ
			new Bank(4, "Unicredit", -1,
					new String[] { "+36303444504" }, //
					new String[] { "[^:]* SpectraNet [^:]*: ([0-9A-Z]*)", "SpectraNet [^:]*: ([0-9 -]*)" },
					R.drawable.unicredit_logo, "HU"),//

			new Bank(5, "ERSTE", -1, new String[] { "+36303444481" }, //
					new String[] { ".* ERSTE NetBank [^:]*: ([0-9]*)" }, R.drawable.erste_logo, "HU"), //

			new Bank(6, "Allianz", -1, new String[] { "+36303444664" }, //
					new String[] { "Az [^:]*: ([0-9]*).* Netbank .*" }, R.drawable.allianz_logo, "HU"), //

			//+36303444455OAC - Online Aktivalasi kod: 633831. Kartyaszam: XX1111; Kedvezmenyezett: AAAAA BBBB www.citibank.hu Tel: +3612888888
			new Bank(7, "Citibank", -1, new String[] { "+36303444455" }, //
					new String[] { "[^:]*: ([0-9]*).*citibank.*" }, R.drawable.citibank_logo, "HU"), //

			new Bank(8, "FHB", -1, new String[] { "+36303444043" }, //
					new String[] { "[^:]*: ([0-9]*-[0-9]*).* FHB" }, R.drawable.fhb_logo, "HU"), //

			new Bank(9, "Budapest Bank", -1, new String[] { "+36309266245" }, //
					new String[] { "[^:]*: ([0-9]*) .*Budapest" }, R.drawable.budapestbank_logo, "HU"), //

			new Bank(10, "MKB", -1, new String[] { "+36707060652", "+36209000652" }, //
					new String[] { "MKB .* jelsz.: ([0-9a-zA-Z]*)" }, R.drawable.mkb_logo, "HU"), //

	};

	static
	{
		Arrays.sort(banks, new Comparator<Bank>()
		{

			@Override
			public int compare( final Bank object1, final Bank object2 )
			{
				return object1.getName().compareTo(object2.getName());
			}
		});
	}

	public static Bank[] getDefaultBanks()
	{//
		return banks;
	}

	/**
	 * This method is defined for only unit testing purposes. This is searching in the default list of 
	 * banks, and not in the database.
	 * @param phoneNumber
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public static Bank findByPhoneNumber( final String phoneNumber )
	{
		return findByPhoneNumber(getDefaultBanks(), phoneNumber);
	}

	public static Bank findByPhoneNumber( final Context context, final String phoneNumber )
	{
		//FIXME implement with direct URI find
		return findByPhoneNumber(getAllBanks(context), phoneNumber);
	}

	public static Bank findByPhoneNumber( final Bank[] banks, final String phoneNumber )
	{
		//filter for Bank phone number
		Bank source = null;
		for ( final Bank bank : banks )
		{
			if ( bank.isBankPhoneNumber(phoneNumber) )
			{
				source = bank;
				break;
			}
		}

		return source;
	}

	private static final char SEPARATOR_CHAR = ',';
	private static final char ESCAPE_CHAR = '"';
	private static final String EMPTY_WORD = "";
	private static final String ESCAPE_CHAR_AS_STRING = String.valueOf(ESCAPE_CHAR);
	private static final String ESCAPE_CHAR_DUPLICATE = ESCAPE_CHAR_AS_STRING + ESCAPE_CHAR_AS_STRING;

	public static String escapeStrings( final String[] strings )
	{
		final StringBuilder builder = new StringBuilder();

		for ( final String string : strings )
		{
			if ( string.indexOf(SEPARATOR_CHAR) >= 0 || string.indexOf(ESCAPE_CHAR) >= 0 )
			{
				final String escaped = string.replace(ESCAPE_CHAR_AS_STRING, ESCAPE_CHAR_DUPLICATE);
				builder.append(ESCAPE_CHAR).append(escaped).append(ESCAPE_CHAR);
			}
			else
			{
				builder.append(string);
			}
			builder.append(SEPARATOR_CHAR);
		}
		return builder.toString();
	}

	public static String[] unescapeStrings( final String string )
	{
		final List<String> result = new ArrayList<String>();

		final int len = string.length();
		boolean wordStarted = false;
		int wordStartIndex = -1;
		boolean wordEscaped = false;

		for ( int i = 0; i < len; i++ )
		{
			if ( !wordStarted )
			{

				if ( string.charAt(i) == SEPARATOR_CHAR )
				{
					result.add(EMPTY_WORD);
				}
				else
				{
					wordStarted = true;
					wordEscaped = string.charAt(i) == ESCAPE_CHAR;
					wordStartIndex = i;
					if ( wordEscaped )
					{
						wordStartIndex++;
					}
				}
			}
			// word started
			else if ( string.charAt(i) == ESCAPE_CHAR )
			{
				if ( !wordEscaped )
					throw new IllegalArgumentException("Cannot be quote here as the word was not started with quote: "
							+ i);

				//quote is here
				if ( i + 1 < len )
				{
					if ( string.charAt(i + 1) == ESCAPE_CHAR )
					{
						//double quote: this is a single qoute in the normal text
						i++;
					}
					else if ( string.charAt(i + 1) == SEPARATOR_CHAR )// this is the closing quote
					{
						final String escaped = string.substring(wordStartIndex, i);
						result.add(escaped.replace(ESCAPE_CHAR_DUPLICATE, ESCAPE_CHAR_AS_STRING));
						wordStarted = false;
						wordEscaped = false;
						wordStartIndex = -1;

						i++; // skip the comma
					}
					else
						//invalid character
						throw new IllegalArgumentException("Invalid character at: " + i);

				}
				else
				//last character of the string -> close current word and finish
				{
					final String escaped = string.substring(wordStartIndex, i);
					result.add(escaped.replace(ESCAPE_CHAR_DUPLICATE, ESCAPE_CHAR_AS_STRING));
					wordStarted = false;
					wordEscaped = false;
					wordStartIndex = -1;
				}
			}
			else if ( string.charAt(i) == SEPARATOR_CHAR )
			{
				if ( wordEscaped )
				{
					//comma in the quotes -> go forward, do nothing here
				}
				else
				// end of the world :)
				{
					final String unescaped = string.substring(wordStartIndex, i);
					result.add(unescaped);
					wordStarted = false;
					wordEscaped = false;
					wordStartIndex = -1;
				}
			}
			else
			{
				//do nothing, go forward
			}
		}

		return result.toArray(new String[result.size()]);
	}

	public static Bank findByUri( final Context context, final Uri uri )
	{//TODO finish
		final Cursor cursor = context.getContentResolver().query(
				Bank.CONTENT_URI,
				new String[] { Bank.F__ID, Bank.F_NAME, Bank.F_VALIDITY, Bank.F_ICON, Bank.F_COUNTRY,
						Bank.F_PHONENUMBERS, Bank.F_EXPRESSIONS }, null, null, Bank.DEFAULT_SORT_ORDER);

		final List<Bank> banks = new ArrayList<Bank>();
		while ( cursor.moveToNext() )
		{
			final int id = cursor.getInt(0);
			final String name = cursor.getString(1);

			//Log.d(Codes.TAG, "Bank read: " + id + " - " + name);
			final int expiry = cursor.getInt(2);
			final int icon = cursor.getInt(3);
			final String country = cursor.getString(4);
			final String phoneNumbers = cursor.getString(5);
			final String expressions = cursor.getString(6);

			final Bank bank = new Bank(id, name, expiry, unescapeStrings(phoneNumbers), unescapeStrings(expressions),
					icon, country);

			banks.add(bank);
		}

		return banks.get(1);
	}

	public static Bank[] getAllBanks( final Context context )
	{
		final Cursor cursor = context.getContentResolver().query(
				Bank.CONTENT_URI,
				new String[] { Bank.F__ID, Bank.F_NAME, Bank.F_VALIDITY, Bank.F_ICON, Bank.F_COUNTRY,
						Bank.F_PHONENUMBERS, Bank.F_EXPRESSIONS }, null, null, Bank.DEFAULT_SORT_ORDER);

		final List<Bank> banks = new ArrayList<Bank>();
		while ( cursor.moveToNext() )
		{
			final int id = cursor.getInt(0);
			final String name = cursor.getString(1);

			Log.d(Codes.TAG, "Bank read: " + id + " - " + name);
			final int expiry = cursor.getInt(2);
			final int icon = cursor.getInt(3);
			final String country = cursor.getString(4);
			final String phoneNumbers = cursor.getString(5);
			final String expressions = cursor.getString(6);

			final Bank bank = new Bank(id, name, expiry, unescapeStrings(phoneNumbers), unescapeStrings(expressions),
					icon, country);

			banks.add(bank);
		}

		return banks.toArray(new Bank[banks.size()]);
	}

	/**
	 * Store in the database.
	 */
	public static void storeBank( final Context context, final Bank b )
	{
		final ContentValues values = new ContentValues();

		values.put(Bank.F_NAME, b.getName());
		values.put(Bank.F_VALIDITY, b.getExpiry());
		values.put(Bank.F_COUNTRY, b.getCountryCode());
		values.put(Bank.F_ICON, b.getIconId());
		values.put(Bank.F_EXPRESSIONS, escapeStrings(b.getExtractExpressions()));
		values.put(Bank.F_PHONENUMBERS, escapeStrings(b.getPhoneNumbers()));

		if ( b.getId() == Bank.UNASSIGNED_ID )
		{
			//created as a new bank
			final Uri uri = context.getContentResolver().insert(Bank.CONTENT_URI, values);
			b.setId(Integer.parseInt(uri.getPathSegments().get(1)));
			Log.d(TAG, "Bank " + b.getName() + " is inserted with id " + b.getId());
		}
		else
		{
			//update the bank
			final Uri thisUri = Bank.CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(b.getId())).build();
			context.getContentResolver().update(thisUri, values, null, null);
			Log.d(TAG, "Bank " + b.getName() + " is updated.");
		}
	}

	public static Bank[] getDefaultBanks( final Context context ) throws ParserConfigurationException, SAXException,
			IOException
	{
		SAXParser parser = null;
		BankDefinitionHandler handler = null;

		final List<Bank> result = new ArrayList<Bank>();

		int index = 0;
		while ( true )
		{
			index++;

			final String fileName = "bankdef" + index + ".xml";
			InputStream open = null;
			try
			{
				open = context.getAssets().open(fileName); //FIXME try to load it dynamically
			}
			catch ( final IOException e )
			{
				Log.d(TAG, "There is no more bank definition XML. Last item: " + ( index - 1 ), e);
				break;
			}

			if ( parser == null )
			{//lazy initialization
				final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				//parserFactory.setValidating(true); - no validation is available on android
				parser = parserFactory.newSAXParser();
				handler = new BankDefinitionHandler();
			}
			handler.reset();
			parser.parse(open, handler);

			final Bank[] parsed = handler.getBanks();

			Log.d(TAG, fileName + " file contained " + parsed.length + " banks.");

			for ( final Bank bank : parsed )
			{
				result.add(bank);
			}
		}
		return result.toArray(new Bank[result.size()]);
	}
}
