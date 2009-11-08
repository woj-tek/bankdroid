package bankdroid.soda;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;

/**
 * @author user
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

	public static final String F__ID = "_id";
	public static final String F_NAME = "name";
	public static final String F_VALIDITY = "validity";
	public static final String F_ICON = "icon";
	public static final String F_COUNTRY = "country";
	public static final String F_PHONENUMBERS = "phonenumbers";
	public static final String F_EXPRESSIONS = "expressions";
	public static final String F_LASTMESSAGE = "lastmsg";
	public static final String F_TIMESTAMP = "lastts";

	public static final String DEFAULT_COUNTRY = "HU";

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

	private int iconId;

	public Bank()
	{
		// empty constructor
		id = UNASSIGNED_ID;
		name = "";
		expiry = -1;
		phoneNumbers = new String[] { "" };//minimum 1 phone number is required
		extractExpressions = new String[] { "" };//minimum 1 pattern is required
		countryCode = DEFAULT_COUNTRY;
		iconId = R.drawable.bankdroid_logo;
	}

	public Bank( final int id, final String name, final int expiry, final String[] phoneNumber,
			final String[] extractExpression, final int iconId, final String countryCode )
	{
		this.id = id;
		this.name = name;
		this.expiry = expiry;
		this.phoneNumbers = phoneNumber;
		this.extractExpressions = extractExpression;
		this.iconId = iconId;
		this.countryCode = countryCode;
	}

	public void addExtractExpression( final String extractExpression )
	{
		final String[] ee = new String[extractExpressions.length + 1];
		System.arraycopy(extractExpressions, 0, ee, 0, extractExpressions.length);
		ee[ee.length - 1] = extractExpression;
		extractExpressions = ee;
		patterns = null;
	}

	public void addPhoneNumber( final String phoneNumber )
	{
		final String[] pn = new String[phoneNumbers.length + 1];
		System.arraycopy(phoneNumbers, 0, pn, 0, phoneNumbers.length);
		pn[pn.length - 1] = phoneNumber;
		phoneNumbers = pn;
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

	public String getCountryCode()
	{
		return countryCode;
	}

	public int getExpiry()
	{
		return expiry;
	}

	public String[] getExtractExpressions()
	{
		return extractExpressions;
	}

	public int getIconId()
	{
		return iconId;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String[] getPhoneNumbers()
	{
		return phoneNumbers;
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

		patterns = null;
		extractExpressions = ee;
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

	public void setCountryCode( final String countryCode )
	{
		this.countryCode = countryCode;
	}

	public void setExpiry( final int expiry )
	{
		this.expiry = expiry;
	}

	public void setIconId( final int iconId )
	{
		this.iconId = iconId;
	}

	public void setId( final int id )
	{
		this.id = id;
	}

	public void setName( final String name )
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
