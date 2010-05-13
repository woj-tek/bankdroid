package bankdroid.start;

import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.AccountService;

public class FakeAccountService implements AccountService
{

	private final Account[] accounts;

	public FakeAccountService( final Account[] accounts )
	{
		this.accounts = accounts;
	}

	@Override
	public Account[] getAccounts()
	{
		return accounts;
	}

	@Override
	public void execute( final Session session ) throws ServiceException
	{
		throw new ServiceException("Invalid call on Fake Service.");
	}

}
