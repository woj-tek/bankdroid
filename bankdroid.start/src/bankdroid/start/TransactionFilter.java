package bankdroid.start;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.csaba.connector.model.Account;
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;

public class TransactionFilter implements Serializable
{
	private static final long serialVersionUID = -1033627827581113341L;

	private Bank bank;
	private Customer customer;
	private Account account;
	private Date from;
	private Date to;

	public TransactionFilter()
	{
		super();
	}

	public TransactionFilter( final Bank bank, final Customer customer, final Account account, final Date from,
			final Date to )
	{
		super();
		this.bank = bank;
		this.customer = customer;
		this.account = account;
		this.from = from;
		this.to = to;
	}

	public Bank getBank()
	{
		return bank;
	}

	public void setBank( final Bank bank )
	{
		this.bank = bank;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer( final Customer customer )
	{
		this.customer = customer;
	}

	public Account getAccount()
	{
		return account;
	}

	public void setAccount( final Account account )
	{
		this.account = account;
	}

	public Date getFrom()
	{
		return from;
	}

	public void setFrom( final Date from )
	{
		this.from = from;
	}

	public Date getTo()
	{
		return to;
	}

	public void setTo( final Date to )
	{
		this.to = to;
	}

	public static TransactionFilter getDefaultFilter()
	{
		final Calendar cal = Calendar.getInstance();
		final Date to = cal.getTime();
		cal.add(Calendar.DATE, -7);
		final Date from = cal.getTime();

		return new TransactionFilter(null, null, null, from, to);
	}
}
