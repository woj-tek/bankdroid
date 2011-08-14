package bankdroid.soda;

import bankdroid.soda.bank.Bank;

public class Code
{

	private Bank bank;
	private String code;
	private String message;
	private String originatingAddress;

	public Bank getBank()
	{
		return bank;
	}

	public void setBank( final Bank bank )
	{
		this.bank = bank;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode( final String code )
	{
		this.code = code;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage( final String message )
	{
		this.message = message;
	}

	public String getOriginatingAddress()
	{
		return originatingAddress;
	}

	public void setOriginatingAddress( final String originatingAddress )
	{
		this.originatingAddress = originatingAddress;
	}

}
