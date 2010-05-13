package bankdroid.start;

import bankdroid.start.ServiceRunner.ServiceListener;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.AccountService;
import com.csaba.connector.service.LogoutService;

public class SessionManager implements ServiceListener
{

	private SessionManager()
	{
		// singleton, do not initialize
	}

	private static SessionManager manager;

	public static SessionManager getInstance()
	{
		if ( manager == null )
			manager = new SessionManager();
		return manager;
	}

	///////METHODS

	private Session session;
	private Account[] accounts;
	private ServiceActivity lastCaller;

	public void setSession( final Session session )
	{
		this.session = session;
		if ( session == null )
		{
			accounts = null;
		}
	}

	public Session getSession()
	{
		return session;
	}

	public void getAccounts( final ServiceActivity caller )
	{
		if ( accounts == null )
		{
			try
			{
				final AccountService accounts = BankServiceFactory.getBankService(session.getBank(),
						AccountService.class);
				lastCaller = caller;
				( new ServiceRunner(caller, this, accounts, session) ).start();
			}
			catch ( final ServiceException e )
			{
				caller.onServiceFailed(null, e);
			}

		}
		else
		{
			caller.onServiceFinished(new FakeAccountService(accounts));
		}
	}

	public void logout( final ServiceActivity caller )
	{
		try
		{
			//dispatch analytics stats here
			caller.dispatch();

			final Session session = SessionManager.getInstance().getSession();

			final LogoutService logout = BankServiceFactory.getBankService(session.getBank(), LogoutService.class);

			lastCaller = caller;
			( new ServiceRunner(caller, this, logout, session) ).start();
		}
		catch ( final ServiceException e )
		{
			caller.onServiceFailed(null, e);
		}
	}

	@Override
	public void onServiceFailed( final BankService service, final Throwable tr )
	{
		lastCaller.onServiceFailed(service, tr);
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		if ( service instanceof LogoutService )
		{
			setSession(null);
		}
		else if ( service instanceof AccountService )
		{
			accounts = ( (AccountService) service ).getAccounts();
		}
		lastCaller.onServiceFinished(service);
	}
}
