package bankdroid.soda;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author user
 * 
 * TODO currently the bank list is hard coded but it should come from a configuration file.
 * TODO phone number matching should rely on the number endings.
 *
 */
public class Bank
{
	private final static Bank[] banks = new Bank[] { //
			new Bank("OTP", 3600, new String[] { "+36309400700", "+36209400700" }, //FIXME phone numbers
					new String[] { "OTPdirekt - [^:]*: ([0-9]*)" }, R.drawable.otp_logo),

			new Bank("KHB", 10800, new String[] { "+36209000703" }, //FIXME phone numbers
					new String[] { ".*K.H MOBILINFO[^:]*: ([a-zA-Z0-9]{6}).*" }, R.drawable.khb_logo),

			new Bank("Unicredit", -1,
					new String[] { "+36303444504" }, //FIXME phone numbers
					new String[] { "[^:]* SpectraNet [^:]*: ([0-9A-Z]*)", "SpectraNet [^:]*: ([0-9 -]*)" },
					R.drawable.khb_logo),//FIXME Logo validity

			new Bank("ERSTE", -1, new String[] { "+36303444481" }, //
					new String[] { ".* ERSTE NetBank [^:]*: ([0-9]*)" }, R.drawable.khb_logo), //FIXME Logo, validity

			new Bank("Allianz", -1, new String[] { "+36303444664" }, //
					new String[] { "Az [^:]*: ([0-9]*).* Netbank .*" }, R.drawable.khb_logo), //FIXME Logo, validity

			new Bank("Citibank", -1, new String[] { "+36303444455" }, //
					new String[] { "[^:]*: ([0-9]*).*citibank.*" }, R.drawable.khb_logo), //FIXME Logo, validity

			new Bank("FHB", -1, new String[] { "+36303444043" }, //
					new String[] { "[^:]*: ([0-9]*-[0-9]*).* FHB" }, R.drawable.khb_logo), //FIXME Logo, validity

			new Bank("BudapestBank", -1, new String[] { "+36309266245" }, //
					new String[] { "[^:]*: ([0-9]*) .*Budapest" }, R.drawable.khb_logo), //FIXME Logo, validity

			new Bank("MKB", -1, new String[] { "+36707060652", "+36209000652" }, //
					new String[] { "MKB .* jelszó: ([0-9a-zA-Z]*)" }, R.drawable.khb_logo), //FIXME Logo, validity, code complexity

	};

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
	private final String id;

	/**
	 * Validity period of an SMS OTP expressed in seconds.
	 */
	private final int otpValidityPeriod;
	/**
	 * One or more phone number can be registered to the Bank.
	 */
	private final String[] phoneNumbers;
	private final String[] extractExpressions;
	/**
	 * Contains compiled regular expressions. Initialized in lazy mode, as most of the banks will be never used on
	 * the same mobile. 
	 */
	private Pattern[] patterns;

	private final int iconId;

	public Bank( final String id, final int otpValidityPeriod, final String[] phoneNumber,
			final String[] extractExpression, final int iconId )
	{
		super();
		this.id = id;
		this.otpValidityPeriod = otpValidityPeriod;
		this.phoneNumbers = phoneNumber;
		this.extractExpressions = extractExpression;
		this.iconId = iconId;
	}

	public boolean isBankPhoneNumber( final String phoneNumber )
	{
		for ( int i = 0; i < phoneNumbers.length; i++ )
		{
			final String pn = phoneNumbers[i];

			if ( pn.equals(phoneNumber) )
				return true;
		}
		return false;
	}

	private void initPatterns()
	{
		if ( patterns == null )
		{
			patterns = new Pattern[extractExpressions.length];
			for ( int i = 0; i < patterns.length; i++ )
			{
				patterns[i] = Pattern.compile(extractExpressions[i]);
			}
		}
	}

	public String getCode( final String message )
	{
		initPatterns();
		for ( int i = 0; i < patterns.length; i++ )
		{
			final Matcher matcher = patterns[i].matcher(message);
			if ( matcher.find() )
				return matcher.group(1);
		}
		return null;
	}

	public String getId()
	{
		return id;
	}

	public int getOtpValidityPeriod()
	{
		return otpValidityPeriod;
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

}
