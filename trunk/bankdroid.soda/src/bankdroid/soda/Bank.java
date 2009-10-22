package bankdroid.soda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author user
 * 
 * TODO currently the bank list is hard coded but it should come from a configuration file.
 * TODO phone number matching should rely on the number endings.
 * TODO extensible default phone number list
 *
 */
public class Bank implements Serializable, Cloneable, Codes
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9140003377538504964L;

	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of banks.
	 */
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/bankdroid.soda.bank";

	/**
	 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single bank.
	 */
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/bankdroid.soda.bank";

	/**
	 * The content:// style URL for this table.
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://bankdroid.soda.Bank/banks");

	public static final String DEFAULT_SORT_ORDER = Bank.F_NAME;

	public static final int UNASSIGNED_ID = -1;

	public static final String F__ID = "_ID";
	public static final String F_NAME = "NAME";
	public static final String F_VALIDITY = "VALIDITY";
	public static final String F_ICON = "ICON";
	public static final String F_COUNTRY = "COUNTRY";
	public static final String F_PHONENUMBERS = "PHONENUMBERS";
	public static final String F_EXPRESSIONS = "EXPRESSIONS";

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
				return object1.name.compareTo(object2.name);
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

	///NON-STATIC MEMBERS
	private int id;
	private String name;

	/**
	 * Validity period of an SMS OTP expressed in seconds.
	 */
	private int expiry;
	/**
	 * One or more phone number can be registered to the Bank.
	 */
	private String[] phoneNumbers;
	private String[] extractExpressions;
	/**
	 * Contains compiled regular expressions. Initialized in lazy mode, as most of the banks will be never used on
	 * the same mobile. 
	 */
	private Pattern[] patterns;
	/**
	 * Country of the Bank.
	 */
	private String countryCode;

	private final int iconId;

	public Bank( final int id, final String name, final int expiry, final String[] phoneNumber,
			final String[] extractExpression, final int iconId, final String countryCode )
	{
		super();
		this.id = id;
		this.name = name;
		this.expiry = expiry;
		this.phoneNumbers = phoneNumber;
		this.extractExpressions = extractExpression;
		this.iconId = iconId;
		this.countryCode = countryCode;
	}

	@Override
	public Object clone()
	{
		final String[] pn = new String[phoneNumbers.length];
		System.arraycopy(phoneNumbers, 0, pn, 0, pn.length);

		final String[] ee = new String[extractExpressions.length];
		System.arraycopy(extractExpressions, 0, ee, 0, ee.length);

		return new Bank(id, name, expiry, pn, ee, iconId, countryCode);
	}

	public void addPhoneNumber( final String phoneNumber )
	{
		final String[] pn = new String[phoneNumbers.length + 1];
		System.arraycopy(phoneNumbers, 0, pn, 0, phoneNumbers.length);
		pn[pn.length - 1] = phoneNumber;
		phoneNumbers = pn;
	}

	public void removePhoneNumber( final int index )
	{
		final String[] pnOld = phoneNumbers;
		final int len = pnOld.length;

		if ( index < 0 || index >= len )
			throw new ArrayIndexOutOfBoundsException("Invalid phone number index: " + index + "(" + len + ")");

		final String[] pn = new String[len - 1];
		int pni = 0;
		for ( int i = 0; i < len; i++ )
		{
			if ( i != index )
			{
				pn[pni] = pnOld[i];
				pni++;
			}
		}

		phoneNumbers = pn;
	}

	public void addExtractExpression( final String extractExpression )
	{
		final String[] ee = new String[extractExpressions.length + 1];
		System.arraycopy(extractExpressions, 0, ee, 0, extractExpressions.length);
		ee[ee.length - 1] = extractExpression;
		extractExpressions = ee;
	}

	public void removeExtractExpression( final int index )
	{
		final String[] eeOld = extractExpressions;
		final int len = eeOld.length;

		if ( index < 0 || index >= len )
			throw new ArrayIndexOutOfBoundsException("Invalid extract expression index: " + index + "(" + len + ")");

		final String[] ee = new String[len - 1];
		int eei = 0;
		for ( int i = 0; i < len; i++ )
		{
			if ( i != index )
			{
				ee[eei] = eeOld[i];
				eei++;
			}
		}

		extractExpressions = ee;
	}

	public boolean isBankPhoneNumber( final String phoneNumber )
	{
		final String[] pn = phoneNumbers;
		final int len = pn.length;
		for ( int i = 0; i < len; i++ )
		{
			if ( pn[i].equals(phoneNumber) )
				return true;
		}
		return false;
	}

	public String extractCode( final String message )
	{
		//lazy init with optimization for performance
		Pattern[] lp = patterns;
		if ( lp == null )
		{
			final String[] ee = extractExpressions;
			final int len = ee.length;

			lp = patterns = new Pattern[len];
			for ( int i = 0; i < len; i++ )
			{
				lp[i] = Pattern.compile(ee[i]);
			}
		}

		final int len = lp.length;

		for ( int i = 0; i < len; i++ )
		{
			final Matcher matcher = lp[i].matcher(message);
			if ( matcher.find() )
				return matcher.group(1);
		}
		return null;
	}

	public String getName()
	{
		return name;
	}

	public int getExpiry()
	{
		return expiry;
	}

	public String[] getPhoneNumbers()
	{
		return phoneNumbers;
	}

	public String[] getExtractExpressions()
	{
		return extractExpressions;
	}

	public int getIconId()
	{
		return iconId;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public int getId()
	{
		return id;
	}

	public void setName( final String name )
	{
		this.name = name;
	}

	public void setExpiry( final int expiry )
	{
		this.expiry = expiry;
	}

	public String getCountryCode()
	{
		return countryCode;
	}

	public void setCountryCode( final String countryCode )
	{
		this.countryCode = countryCode;
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

	String getExpressionsString()
	{
		return escapeStrings(extractExpressions);
	}

	String getPhoneNumbersString()
	{
		return escapeStrings(phoneNumbers);
	}

	void setExpressionsString( final String expressionString )
	{
		extractExpressions = unescapeStrings(expressionString);
	}

	void setPhoneNumbersString( final String phoneNumberString )
	{
		phoneNumbers = unescapeStrings(phoneNumberString);
	}

	/**
	 * Store in the database.
	 */
	public void store( final Context context )
	{
		final ContentValues values = new ContentValues();

		values.put(F_NAME, getName());
		values.put(F_VALIDITY, getExpiry());
		values.put(F_COUNTRY, getCountryCode());
		values.put(F_ICON, getIconId());
		values.put(F_EXPRESSIONS, getExpressionsString());
		values.put(F_PHONENUMBERS, getPhoneNumbersString());

		if ( id == UNASSIGNED_ID )
		{
			//created as a new bank
			final Uri uri = context.getContentResolver().insert(CONTENT_URI, values);
			id = Integer.parseInt(uri.getPathSegments().get(1));
			Log.d(TAG, "Bank " + name + " is inserted with id " + id);
		}
		else
		{
			//update the bank
			final Uri thisUri = CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(getId())).build();
			context.getContentResolver().update(thisUri, values, null, null);
			Log.d(TAG, "Bank " + name + " is updated.");
		}
	}

	public static Bank findByUri( final Context context, final Uri uri )
	{//TODO finish
		final Cursor cursor = context.getContentResolver().query(CONTENT_URI,
				new String[] { F__ID, F_NAME, F_VALIDITY, F_ICON, F_COUNTRY, F_PHONENUMBERS, F_EXPRESSIONS }, null,
				null, DEFAULT_SORT_ORDER);

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
		final Cursor cursor = context.getContentResolver().query(CONTENT_URI,
				new String[] { F__ID, F_NAME, F_VALIDITY, F_ICON, F_COUNTRY, F_PHONENUMBERS, F_EXPRESSIONS }, null,
				null, DEFAULT_SORT_ORDER);

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

}
