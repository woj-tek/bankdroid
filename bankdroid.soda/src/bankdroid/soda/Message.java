package bankdroid.soda;

import java.util.Date;

public class Message
{
	Bank bank;
	String message;
	Date timestamp;

	public Message( final Bank bank, final String message, final Date timestamp )
	{
		super();
		this.bank = bank;
		this.message = message;
		this.timestamp = timestamp;
	}

}