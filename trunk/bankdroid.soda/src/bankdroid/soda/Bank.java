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
	public static Bank[] getAvailableBanks()
	{//
		final Bank[] banks = new Bank[] { //
		new Bank("OTP Bank", 1800, new String[] { "+36201000000" }, //
				new String[] { "OTP .*: ([a-zA-Z0-9]*) .*" }, R.drawable.otp_logo),

		new Bank("K&H Bank", 1800, new String[] { "+36202000000" }, //
				new String[] { "KHB .*: ([a-zA-Z0-9]*) .*" }, R.drawable.khb_logo),

		new Bank("ERSTE Bank", 1800, new String[] { "+36303444481" }, //
				new String[] { ".* ERSTE NetBank [^:]*: ([0-9]*)" }, R.drawable.khb_logo), //FIXME Logo, validity

		};
		return banks;
	}
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
