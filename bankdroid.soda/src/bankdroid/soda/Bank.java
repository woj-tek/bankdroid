package bankdroid.soda;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author user
 * 
 * TODO currently the bank list is hard coded but it should come from a configuration file.
 * TODO phone number matching should rely on the number endings.
 *
 */
public class Bank implements Serializable
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

	private final static Bank[] banks = new Bank[] { //
			new Bank(1, "OTP", 3600, new String[] { "+36309400700", "+36209400700" }, //
					new String[] { "OTPdirekt - [^:]*: ([0-9]*)" }, R.drawable.otp2_logo),

			new Bank(2, "KHB", 1800, new String[] { "+36209000703" }, //
					new String[] { ".*K.H e-bank[^:]*: ([a-zA-Z0-9]{6}).*" }, R.drawable.kh_logo),

			//+36707060660Az On Raiffeisen DirektNet egyszer hasznalatos jelszava: 76037367 Raiffeisen Bank Zrt.
			new Bank(3, "Raiffeisen Bank", 3600, new String[] { "+36707060660" }, //
					new String[] { ".* Raiffeisen DirektNet .* jelszava: ([0-9]*) .*" }, R.drawable.raiffeisen_logo),

			new Bank(4, "Unicredit", -1,
					new String[] { "+36303444504" }, //
					new String[] { "[^:]* SpectraNet [^:]*: ([0-9A-Z]*)", "SpectraNet [^:]*: ([0-9 -]*)" },
					R.drawable.unicredit_logo),//

			new Bank(5, "ERSTE", -1, new String[] { "+36303444481" }, //
					new String[] { ".* ERSTE NetBank [^:]*: ([0-9]*)" }, R.drawable.erste_logo), //

			new Bank(6, "Allianz", -1, new String[] { "+36303444664" }, //
					new String[] { "Az [^:]*: ([0-9]*).* Netbank .*" }, R.drawable.allianz_logo), //

			new Bank(7, "Citibank", -1, new String[] { "+36303444455" }, //
					new String[] { "[^:]*: ([0-9]*).*citibank.*" }, R.drawable.citibank_logo), //

			new Bank(8, "FHB", -1, new String[] { "+36303444043" }, //
					new String[] { "[^:]*: ([0-9]*-[0-9]*).* FHB" }, R.drawable.fhb_logo), //

			new Bank(9, "Budapest Bank", -1, new String[] { "+36309266245" }, //
					new String[] { "[^:]*: ([0-9]*) .*Budapest" }, R.drawable.budapestbank_logo), //

			new Bank(10, "MKB", -1, new String[] { "+36707060652", "+36209000652" }, //
					new String[] { "MKB .* jelsz.: ([0-9a-zA-Z]*)" }, R.drawable.mkb_logo), //

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

	public static Bank[] getAvailableBanks()
	{//
		return banks;
	}

	public static Bank findByPhoneNumber( final String phoneNumber )
	{
		//filter for Bank phone number
		final Bank[] banks = Bank.getAvailableBanks();
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
	private final int id;
	private final String name;

	/**
	 * Validity period of an SMS OTP expressed in seconds.
	 */
	private final int expiry;
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

	private final int iconId;

	public Bank( final int id, final String name, final int expiry, final String[] phoneNumber,
			final String[] extractExpression, final int iconId )
	{
		super();
		this.id = id;
		this.name = name;
		this.expiry = expiry;
		this.phoneNumbers = phoneNumber;
		this.extractExpressions = extractExpression;
		this.iconId = iconId;
	}

	public void addPhoneNumber( final String phoneNumber )
	{
		final String[] pn = new String[phoneNumbers.length + 1];
		System.arraycopy(phoneNumber, 0, pn, 0, phoneNumbers.length);
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

	public void removeExtratExpression( final int index )
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

	public String[] getExtractExpression()
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
}
