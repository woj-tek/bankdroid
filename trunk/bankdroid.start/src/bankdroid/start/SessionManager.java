package bankdroid.start;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import bankdroid.start.ServiceRunner.ServiceListener;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.AccountService;
import com.csaba.connector.service.LogoutService;

public class SessionManager implements ServiceListener, Codes
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

	public void setSession( final ServiceActivity activity, final Session session )
	{
		this.session = session;

		final Context context = activity;
		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if ( session == null )
		{
			accounts = null;

			//hide notification
			nm.cancel(NOTIFICATION_ACTIVE_SESSION);
		}
		else
		{
			//show notification
			final int icon = android.R.drawable.stat_notify_error;
			final long when = System.currentTimeMillis();

			final Notification notification = new Notification(icon, null, when);

			final Intent notificationIntent = new Intent(context, MainActivity.class);
			notificationIntent.setAction(Intent.ACTION_VIEW);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

			final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			notification.setLatestEventInfo(context, context.getString(R.string.app_name), context
					.getString(R.string.warnActiveSession), contentIntent);
			notification.flags |= Notification.FLAG_ONGOING_EVENT;

			//display notification
			nm.notify(NOTIFICATION_ACTIVE_SESSION, notification);
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
			setSession(lastCaller, null);
		}
		else if ( service instanceof AccountService )
		{
			accounts = ( (AccountService) service ).getAccounts();
		}
		lastCaller.onServiceFinished(service);
	}
}
