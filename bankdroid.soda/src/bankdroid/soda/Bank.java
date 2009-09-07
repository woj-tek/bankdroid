package bankdroid.soda;

import java.util.regex.Pattern;

public class Bank
{
	public static Bank[] getAvailableBanks()
	{//TODO currently the bank list is hard coded but it should come from a configuration file.
		final Bank[] banks = new Bank[] { //
		new Bank("OTP Bank", 1800, "+36201000000", "OTP .*: ([a-zA-Z0-9]*) .*", R.drawable.otp_logo),
				new Bank("K&H Bank", 1800, "+36202000000", "KHB .*: ([a-zA-Z0-9]*) .*", R.drawable.khb_logo), };
		return banks;
	}
	private final String id;
	/**
	 * Validity period of an SMS OTP expressed in seconds.
	 */
	private final int otpValidityPeriod;
	private final String phoneNumber;
	private final String extractExpression;
	private final Pattern extractPattern;

	private final int iconId;

	public Bank( final String id, final int otpValidityPeriod, final String phoneNumber,
			final String extractExpression, final int iconId )
	{
		super();
		this.id = id;
		this.otpValidityPeriod = otpValidityPeriod;
		this.phoneNumber = phoneNumber;
		this.extractExpression = extractExpression;
		this.iconId = iconId;
		extractPattern = Pattern.compile(extractExpression);
	}

	public String getId()
	{
		return id;
	}

	public int getOtpValidityPeriod()
	{
		return otpValidityPeriod;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public String getExtractExpression()
	{
		return extractExpression;
	}

	public Pattern getExtractPattern()
	{
		return extractPattern;
	}

	public int getIconId()
	{
		return iconId;
	}

}
