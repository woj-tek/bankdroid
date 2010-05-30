package bankdroid.start;

import java.text.MessageFormat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import bankdroid.start.ServiceRunner.ServiceListener;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.I18NServiceException;
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

	/**
	 * Warning period indicates when the customer will be notified before the session timeout starts.
	 * During the warning period a notification will be updated in every second. 
	 */
	public final static long WARNING_PERIOD = 10 * 1000;
	public final static long WARNING_TICKS = 1 * 1000;

	private final static int HANDLER_TIMEOUT = 23421;

	private Session session;
	private Account[] accounts;
	private ServiceActivity lastCaller;
	private BankService activeService;
	private long lastSessionPingTS;

	private Handler timeoutHandler;

	public void setSession( final Context context, final Session session )
	{
		this.session = session;

		final String st = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SESSION_TIMEOUT,
				DEFAULT_SESSION_TIMEOUT);
		final long sessionTimeout = Long.parseLong(st) * 60000L;

		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if ( session == null )
		{
			accounts = null;
			lastSessionPingTS = 0;

			//hide notification
			nm.cancel(NOTIFICATION_ACTIVE_SESSION);
			nm.cancel(NOTIFICATION_SESSION_TIMEOUT);

			//clear timeout thread
			if ( timeoutHandler != null )
				timeoutHandler.removeMessages(HANDLER_TIMEOUT);
			timeoutHandler = null;
		}
		else
		{
			nm.cancel(NOTIFICATION_SESSION_TIMEOUT_EXPIRED);

			//show notification
			final int icon = R.drawable.bankdroid_status_icon;
			final long when = System.currentTimeMillis();
			lastSessionPingTS = when;

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

			//start timeout handler here
			timeoutHandler = new Handler()
			{
				@Override
				public void handleMessage( final Message msg )
				{
					super.handleMessage(msg);

					if ( msg.what == HANDLER_TIMEOUT )
					{
						final long now = System.currentTimeMillis();
						final long diff = now - lastSessionPingTS;
						if ( diff >= sessionTimeout )
						{
							//logout here
							silentLogout(context);
						}
						else if ( diff >= sessionTimeout - WARNING_PERIOD )
						{
							final Notification notification = new Notification(android.R.drawable.stat_notify_error,
									null, System.currentTimeMillis());

							final Intent notificationIntent = new Intent(context, MainActivity.class);
							notificationIntent.setAction(Intent.ACTION_VIEW);
							notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

							final PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
									notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

							notification.setLatestEventInfo(context, context.getString(R.string.app_name),
									MessageFormat.format(context.getString(R.string.warnSessionTimeout),
											(int) ( ( sessionTimeout - diff ) / 1000 )), contentIntent);
							notification.flags |= Notification.FLAG_ONGOING_EVENT;

							//display notification
							nm.notify(NOTIFICATION_SESSION_TIMEOUT, notification);

							//start ticking here, and showing a notification
							timeoutHandler.sendMessageDelayed(timeoutHandler.obtainMessage(HANDLER_TIMEOUT),
									WARNING_TICKS);
						}
						else
						{
							//continue waiting
							timeoutHandler.sendMessageDelayed(timeoutHandler.obtainMessage(HANDLER_TIMEOUT), diff
									- ( sessionTimeout - WARNING_PERIOD ));

							// clear warning notification if there is any.
							nm.cancel(NOTIFICATION_SESSION_TIMEOUT);
						}
					}
				}
			};
			timeoutHandler.sendMessageDelayed(timeoutHandler.obtainMessage(HANDLER_TIMEOUT), sessionTimeout
					- WARNING_PERIOD);
		}
	}

	/**
	 * This method can be used to keep the session alive, and postpone session timeout.
	 * This is called automatically internally in SessionManager, but it can be called from outside
	 * too, if there is some long term activity which doesn't call anything on the SessionManager.
	 */
	public void pingSession()
	{
		lastSessionPingTS = System.currentTimeMillis();
	}

	public Session getSession()
	{
		pingSession();

		return session;
	}

	public void getAccounts( final ServiceActivity caller )
	{
		pingSession();

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

	public void silentLogout( final Context context )
	{
		try
		{
			//dispatch analytics stats here
			final Session session = getSession();

			final LogoutService logout = BankServiceFactory.getBankService(session.getBank(), LogoutService.class);
			logout.execute(session);
			Log.d(TAG, "Silent logout was succsfull: " + logout.getClass().getName());
		}
		catch ( final Exception e )
		{
			Log.e(TAG, "Silent logout failed.", e);
		}

		setSession(context, null);

		//display notification
		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		final int icon = android.R.drawable.stat_notify_error;
		final long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, null, when);

		final Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.setAction(Intent.ACTION_VIEW);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(context, context.getString(R.string.app_name), context
				.getString(R.string.warnSessionTimeouted), contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		nm.notify(NOTIFICATION_SESSION_TIMEOUT_EXPIRED, notification);
	}

	public void logout( final ServiceActivity caller )
	{
		try
		{

			//dispatch analytics stats here
			if ( caller != null )
				caller.dispatch();

			final Session session = getSession();

			final LogoutService logout = BankServiceFactory.getBankService(session.getBank(), LogoutService.class);

			lastCaller = caller;
			( new ServiceRunner(caller, this, logout, session) ).start();
		}
		catch ( final ServiceException e )
		{
			if ( caller != null )
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

	public void clearActiveService()
	{
		this.activeService = null;
	}

	public void setActiveService( final BankService activeService ) throws ServiceException
	{
		pingSession();

		if ( this.activeService != null && activeService != null )
		{
			throw new I18NServiceException(this, "errActiveService");
		}

		this.activeService = activeService;
	}

	public BankService getActiveService()
	{
		return activeService;
	}

	public boolean isLoggedIn()
	{
		pingSession();

		return session != null;
	}
}
