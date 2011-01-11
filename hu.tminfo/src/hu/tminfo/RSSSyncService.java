package hu.tminfo;

import hu.tminfo.widget.PortalWidgetProvider;
import hu.tminfo.widget.Widget11Provider;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import bankdroid.rss.RSSStream;

/**
 * @author Gabe
 */
public class RSSSyncService extends Service implements Runnable, Codes
{
	/**
	 * Minor time period that is waited when synch has to be started immediately (for example after long period of network 
	 * unavailability. Value is defined in millisecs.
	 */
	private static final long IMMEDIATE_DELAY = 7000;

	/**
	 * After this timeout the synchronization is interrupted.
	 */
	private static final long WAKE_LOCK_TIMEOUT = 3 * 60 * 1000L; // 3 minutes timeout
	private static final int RELEASE_WAKE_LOCK = 2343242;

	private static PowerManager.WakeLock lockStatic = null;
	private static Object lockSyncObject = new Object();
	private static int wakeLockCounter = 0;
	private static Handler wakeLockHandler;

	public static void acquireLock( final Context context )
	{
		synchronized ( lockSyncObject )
		{
			if ( lockStatic == null )
			{
				final PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

				lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CPU_WAKE_LOCK);
				lockStatic.setReferenceCounted(true);

				wakeLockCounter = 0;
			}

			if ( wakeLockCounter < 1 )
			{

				lockStatic.acquire();

				wakeLockHandler = new Handler()
				{
					@Override
					public void handleMessage( final Message msg )
					{
						super.handleMessage(msg);

						if ( msg.what == RELEASE_WAKE_LOCK )
						{
							if ( lockStatic.isHeld() )
							{
								lockStatic.release();
								wakeLockCounter--;
								debugNotification(context, NOTIFICATION_WAKELOCK, "WakeLock.release() on timeout - "
										+ wakeLockCounter);
							}
						}
					}
				};

				wakeLockHandler.sendMessageDelayed(wakeLockHandler.obtainMessage(RELEASE_WAKE_LOCK), WAKE_LOCK_TIMEOUT);

				wakeLockCounter++;

				debugNotification(context, NOTIFICATION_WAKELOCK, "WakeLock.acquire() - " + wakeLockCounter);
			}
		}
	}

	public static void releaseLock( final Context context )
	{
		synchronized ( lockSyncObject )
		{
			if ( lockStatic != null )
			{
				if ( lockStatic.isHeld() )
				{
					lockStatic.release();

					wakeLockCounter--;

					debugNotification(context, NOTIFICATION_WAKELOCK, "WakeLock.release() - " + wakeLockCounter);
				}
			}
		}
	}

	/**
	 * Use this static method to start the service.
	 * 
	 * @param context
	 */
	public static void startService( final Context context, final String action )
	{
		final Intent normalStart = new Intent(action);
		normalStart.setClass(context, RSSSyncService.class);
		context.startService(normalStart);
	}

	public static void schedule( final Context context, final String newFrequency )
	{
		final long frequency = getFrequency(context, newFrequency);
		if ( frequency == 0 )
		{
			Log.w(TAG, "Skip schedule as it is manually scheduled.");
			return;
		}

		//skip schedule if it is disconnected.
		final ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
		if ( netInfo == null || netInfo.getState() == NetworkInfo.State.DISCONNECTED )
		{
			Log.d(TAG, "No schedule due to lack of network connection.");
			return;
		}

		final long rightNow = System.currentTimeMillis();

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		//verify last success: if there was no succesful synch since a while - within the 
		//frequency period, synch is started immediately with some delay.
		long nextTime = rightNow + frequency;
		final long lastSuccesfulSynch = preferences.getLong(PREF_LAST_SUCCESFUL_SYCNH, -1);
		if ( rightNow - lastSuccesfulSynch > frequency )
		{
			nextTime = rightNow + IMMEDIATE_DELAY;
		}

		//check last schedule: if it is already in the prefered time period, there is no need for reschedule.
		final long lastSchedule = preferences.getLong(PREF_NEXT_SCHEDULE, -1);
		if ( lastSchedule > rightNow && lastSchedule <= nextTime )
		{
			Log.d(TAG, "Refresh is already scheduled for the appropriate time range.");
			return;
		}

		//schedule alarm according to the calculated time.
		final Intent i = new Intent(context, RSSServiceStartReceiver.class);
		i.setAction(ACTION_SYNCH_NOW);
		final PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

		final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, nextTime, pi);

		//set last schedule
		final Editor prefEditor = preferences.edit();
		prefEditor.putLong(PREF_NEXT_SCHEDULE, nextTime);
		prefEditor.commit();

		Log.d(TAG, "Feed synch activated to " + nextTime + " (" + ( nextTime - rightNow ) + ")");
	}

	public static void clearSchedule( final Context context )
	{
		final Intent i = new Intent(context, RSSServiceStartReceiver.class);
		i.setAction(ACTION_SYNCH_NOW);
		final PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

		final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pi);

		//clear last schedule pref
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		final Editor prefEditor = preferences.edit();
		prefEditor.putLong(PREF_NEXT_SCHEDULE, -1);
		prefEditor.commit();

		Log.d(TAG, "Feed synch cleared.");
	}

	public final static int CMD_SHOW_REFRESH = 1;
	public final static int CMD_HIDE_REFRESH = 2;
	public final static int CMD_SHOW_NEWITEM = 3;

	private Handler handler;

	private boolean feedChanged = false;
	private boolean running = false;
	private boolean manualStart;

	private NotificationManager nm;

	private RefreshListener listener;

	//contains URL that was added/removed from Preferences
	private int[] feedRemoved = null;
	private int[] feedAdded = null;

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder
	{
		RSSSyncService getService()
		{
			return RSSSyncService.this;
		}
	}
	private final IBinder mBinder = new LocalBinder();

	private Thread synchThread;

	@Override
	public void onCreate()
	{
		super.onCreate();
		debugNotification(NOTIFICATION_SERVICE_LIFECYCLE, "Service onCreate() called.");

		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		//create handler (to show icons, notifications, read preferences, etc...)
		handler = new Handler()
		{
			@Override
			public void handleMessage( final Message msg )
			{
				super.handleMessage(msg);

				if ( msg.what == CMD_SHOW_REFRESH )
				{
					showRefresh();
				}
				else if ( msg.what == CMD_HIDE_REFRESH )
				{
					clearRefresh();
				}
				else if ( msg.what == CMD_SHOW_NEWITEM )
				{
					showNewItem();
				}
			}
		};
	}

	@Override
	public void onStart( final Intent intent, final int startId )
	{
		super.onStart(intent, startId);

		debugNotification(NOTIFICATION_SERVICE_LIFECYCLE, "Service onStart() called.");

		boolean start = false;
		if ( intent.getAction().equals(ACTION_SYNCH_NOW) )
		{
			start = true;
		}
		else if ( intent.getAction().equals(ACTION_MANUAL_START) )
		{
			start = manualStart = true;
		}
		else if ( intent.getAction().equals(ACTION_FEED_CHANGED) )
		{
			//start service asap with flag clears database
			start = feedChanged = true;
			feedAdded = intent.getIntArrayExtra(EXTRA_FEEDS_ADDED);
			feedRemoved = intent.getIntArrayExtra(EXTRA_FEEDS_REMOVED);
		}

		if ( start && !isRunning() )
		{
			clearSchedule(getBaseContext());
			execute();
		}
		if ( start && running )
		{
			Log.d(TAG, "Service start called, but it is already running.");
		}
	}

	/**
	 * Updates the frequency from preferences or from the parameter.
	 * @param newFrequency 
	 * @return True, if the current and the new frequency are not the same.
	 */
	private static long getFrequency( final Context context, final String newFrequency )
	{
		String frequency = newFrequency;
		if ( frequency == null )
		{
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			frequency = preferences.getString(PREF_FREQUENCY, DEFAULT_FREQUENCY);
		}
		final long frequencyInMillis = Long.parseLong(frequency) * 60 * 1000;

		return frequencyInMillis;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		debugNotification(NOTIFICATION_SERVICE_LIFECYCLE, "Service onDestroy() called.");
		if ( running )
		{
			synchThread.interrupt();
			RSSStream.abortSynch(this);
		}
	}

	@Override
	public IBinder onBind( final Intent intent )
	{
		debugNotification(NOTIFICATION_SERVICE_LIFECYCLE, "Service onBind() called.");
		return mBinder;
	}

	private void execute()
	{
		synchThread = new Thread(this);
		synchThread.start();
	}

	/**
	 * This method does the actual synch from the stream. It is also handling notifications and 
	 * able to clean up the dB before refresh (this may be necessary, if the feed is changed).
	 * @param cleanUpDB if true, the all cahced item will be deleted before the refresh.
	 */
	@Override
	public void run()
	{
		final long start = System.currentTimeMillis();

		setRunning(true);
		try
		{
			if ( listener != null )
				listener.onStartRefresh();

			//check network state before start.
			final ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(
					Context.CONNECTIVITY_SERVICE);
			final NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
			if ( netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED )
			{
				//do a refresh
				final boolean backgroundData = connectivityManager.getBackgroundDataSetting();

				//manual start and feed changed is not a background process
				if ( backgroundData || manualStart || feedChanged )
				{
					final SharedPreferences preferences = PreferenceManager
							.getDefaultSharedPreferences(getBaseContext());

					final boolean cleanDB = preferences.getBoolean(PREF_CLEANDB, false);

					//start network communication and refresh
					try
					{
						handler.sendMessage(handler.obtainMessage(CMD_SHOW_REFRESH));

						final Context context = getBaseContext();
						if ( cleanDB )
						{
							RSSStream.deleteItems(context);
						}
						else if ( feedChanged )
						{
							if ( feedRemoved != null )
								RSSStream.deleteChannels(context, feedRemoved);
						}

						final boolean newItems = RSSStream.synchronize(context, feedAdded, RSSSyncService.this);

						if ( newItems )
						{
							// Push update for this widget to the home screen
							final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
							PortalWidgetProvider.updateWidgets(context, appWidgetManager);
							Widget11Provider.updateWidgets(context, appWidgetManager);

							if ( preferences.getBoolean(PREF_NOTIFICATION, DEFAULT_NOTIFICATION) )
							{
								handler.sendMessage(handler.obtainMessage(CMD_SHOW_NEWITEM));
							}
						}

						debugNotification(NOTIFICATION_THREAD_LIFECYCLE, "Synch item found: " + newItems);

						//dispatch tracker events
						TrackedActivity.dispatch(getBaseContext());
					}
					catch ( final Exception e )
					{
						Log.e(TAG, "Unable to synchronise the feeds.", e);
						debugNotification(NOTIFICATION_ERRORS, "Exception: " + e);
					}
					finally
					{
						handler.sendMessage(handler.obtainMessage(CMD_HIDE_REFRESH));
					}

					//store last synch time

					final Editor prefEditor = preferences.edit();
					prefEditor.putLong(PREF_LAST_SUCCESFUL_SYCNH, System.currentTimeMillis());
					prefEditor.putBoolean(PREF_CLEANDB, false);
					prefEditor.commit();
				}
			}

			//clear state
			feedChanged = false;
			feedRemoved = null;
			feedAdded = null;
			manualStart = false;

			schedule(getBaseContext(), null);

			stopSelf();
		}
		finally
		{
			setRunning(false);

			releaseLock(getBaseContext());

			synchThread = null;

			if ( listener != null )
			{
				listener.onStopRefresh();
			}
			Log.d(TAG, "Full refresh time: " + ( System.currentTimeMillis() - start ));
		}
	}

	private void debugNotification( final int level, final String message )
	{
		debugNotification(getBaseContext(), level, message);
	}

	private static void debugNotification( final Context context, final int level, final String message )
	{
		Log.d(TAG, message);

		if ( !PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_DEBUG, DEFAULT_DEBUG) )
			return;

		//display notification
		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		//create notification
		final int icon = android.R.drawable.stat_sys_warning;
		final long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, message, when);

		final Intent notificationIntent = new Intent(context, ItemListActivity.class);
		notificationIntent.setAction(Intent.ACTION_VIEW);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(context, "Debug", message, contentIntent);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		//display notification
		nm.notify(level, notification);
	}

	private void setRunning( final boolean running )
	{
		this.running = running;
	}

	public boolean isRunning()
	{
		return running;
	}

	private void showRefresh()
	{
		Log.d(TAG, "Show refresh icon.");
		//show new icon in the notification bar
		final int icon = android.R.drawable.stat_notify_sync;
		final long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, null, when);

		final Intent notificationIntent = new Intent(this, ItemListActivity.class);
		notificationIntent.setAction(Intent.ACTION_VIEW);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(this, getString(R.string.notificationTitle),
				getString(R.string.notificationBody), contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		//display notification
		nm.notify(NOTIFICATION_REFRESH, notification);

	}

	public void clearRefresh()
	{
		Log.d(TAG, "HIDE refresh icon.");
		nm.cancel(NOTIFICATION_REFRESH);
	}

	private void showNewItem()
	{
		Log.d(TAG, "Show NEW ITEM icon.");

		//show new icon in the notification bar
		final int icon = R.drawable.notification;
		final long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, getString(R.string.notificationBody2), when);

		final Intent notificationIntent = new Intent(this, ItemListActivity.class);
		notificationIntent.setAction(Intent.ACTION_VIEW);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(this, getString(R.string.notificationTitle),
				getString(R.string.notificationBody2), contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		//display notification
		nm.notify(NOTIFICATION_NEWITEM, notification);
	}

	public void setRefreshListener( final RefreshListener listener )
	{
		this.listener = listener;
	}

	public interface RefreshListener
	{
		void onStartRefresh();

		void onStopRefresh();
	}

	public void forceClose()
	{
	}
}
