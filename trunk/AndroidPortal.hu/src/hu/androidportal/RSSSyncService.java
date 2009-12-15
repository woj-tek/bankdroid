package hu.androidportal;

import hu.androidportal.rss.RSSStream;

import java.net.URL;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * TODO listen for internet connection to suspend/resume synch
 * TODO listen for phone start up to start synch
 * 
 * TODO remove 1 minute frequency
 * TODO add menu to the item view activity: share link, preferences, about
 * 
 * FIXME change scheduling to use ALARM_MANAGER
 * FIXME check the ConnectivityManager.getBackgroudDataService() state
 * @author Gabe
 */
public class RSSSyncService extends Service implements Runnable, Codes
{
	/**
	 * Use this static method to start the service.
	 * 
	 * @param context
	 */
	public static void startService( final Context context )
	{
		context.startService(new Intent(context, RSSSyncService.class));
	}

	public final static int CMD_SHOW_REFRESH = 1;
	public final static int CMD_HIDE_REFRESH = 2;
	public final static int CMD_SHOW_NEWITEM = 3;

	private Handler handler;

	private String feed;
	private URL feedUrl;
	private String frequency;
	private long frequencyInMillis;

	private boolean feedChanged = false;
	private boolean running = false;

	@Override
	public void onCreate()
	{
		super.onCreate();
		debugNotification(NOTIFICATION_SERVICE_LIFECYCLE, "Service onCreate() called.");

		//initialize preferences
		updateFrequency(null);

		updateFeed(null);

		//create handler (to show icons, notifications, read preferences, etc...)
		handler = new Handler()
		{
			@Override
			public void handleMessage( final Message msg )
			{
				super.handleMessage(msg);

				final Context context = getBaseContext();
				final NotificationManager nm = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

				if ( msg.what == CMD_SHOW_REFRESH )
				{
					//show refresh icon in notification bar
					Log.d(TAG, "Show refresh icon.");

					//show new icon in the notification bar
					final int icon = android.R.drawable.stat_notify_sync;
					final long when = System.currentTimeMillis();

					final Notification notification = new Notification(icon, "", when);

					final Intent notificationIntent = new Intent(context, ItemListActivity.class);
					notificationIntent.setAction(Intent.ACTION_VIEW);
					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

					final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					notification.setLatestEventInfo(context, "AndroidPortal.hu",
							"Frissítem az AndroidPortal.hu híreit...", contentIntent);
					notification.flags |= Notification.FLAG_ONGOING_EVENT;

					//display notification
					nm.notify(NOTIFICATION_REFRESH, notification);
				}
				else if ( msg.what == CMD_HIDE_REFRESH )
				{
					//hide refresh icon in notification bar
					Log.d(TAG, "HIDE refresh icon.");

					nm.cancel(NOTIFICATION_REFRESH);
				}
				else if ( msg.what == CMD_SHOW_NEWITEM )
				{
					Log.d(TAG, "Show NEW ITEM icon.");

					//show new icon in the notification bar
					final int icon = android.R.drawable.stat_notify_chat;
					final long when = System.currentTimeMillis();

					final Notification notification = new Notification(icon,
							"Új cikk érkezett az AndroidPortal.hu-ról.", when);

					final Intent notificationIntent = new Intent(context, ItemListActivity.class);
					notificationIntent.setAction(Intent.ACTION_VIEW);
					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

					final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					notification.setLatestEventInfo(context, "AndroidPortal.hu",
							"Új cikk érkezett az AndroidPortal.hu-ról.", contentIntent);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;

					//display notification
					nm.notify(NOTIFICATION_NEWITEM, notification);

				}
			}
		};
	}

	/**
	 * Updates the feed url from preferences or from the parameter.
	 * @return True, if the current and the new urls are not the same.
	 */
	private boolean updateFeed( String feed )
	{
		if ( feed == null )
		{
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			feed = preferences.getString(PREF_FEED, DEFAULT_FEED);
		}

		if ( feed.equals(this.feed) )
			return false;

		try
		{
			Log.d(TAG, "Setting feed to: " + feed);
			feedUrl = new URL(feed);
			this.feed = feed;

			return true;
		}
		catch ( final Exception e )
		{
			Log.e(TAG, "Unparseable feed URL:" + feed, e);
			return false;
		}
	}

	/**
	 * Updates the frequency from preferences or from the parameter.
	 * @return True, if the current and the new frequency are not the same.
	 */
	private boolean updateFrequency( String frequency )
	{
		if ( frequency == null )
		{
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			frequency = preferences.getString(PREF_FREQUENCY, DEFAULT_FREQUENCY);
		}

		if ( frequency.equals(this.frequency) )
			return false;

		this.frequency = frequency;
		frequencyInMillis = Long.parseLong(frequency) * 60 * 1000;

		return true;
	}

	@Override
	public void onStart( final Intent intent, final int startId )
	{
		super.onStart(intent, startId);

		debugNotification(NOTIFICATION_SERVICE_LIFECYCLE, "Service onStart() called.");

		boolean stopService = false;
		if ( intent.getAction().equals(ACTION_MANUAL_START) )
		{
			//start synch immediately in a seperate thread.
			if ( !running )
			{
				clearSchedule();
				execute();
			}
		}
		else if ( intent.getAction().equals(ACTION_FEED_CHANGED) )
		{
			//start service asap with flag clears database
			feedChanged = updateFeed(intent.getStringExtra(PREF_FEED));
			if ( feedChanged )
			{
				clearSchedule();
				execute();
			}
		}
		else if ( intent.getAction().equals(ACTION_FREQ_CHANGED) )
		{
			if ( updateFrequency(intent.getStringExtra(PREF_FREQUENCY)) )
			{
				clearSchedule();
				schedule();
			}
			stopService = true;
		}
		else if ( intent.getAction().equals(ACTION_NORMAL_START) )
		{
			if ( !isScheduled() )
			{
				schedule();
			}
			stopService = true;
		}
		else if ( intent.getAction().equals(ACTION_SYNCH_NOW) )
		{
			if ( !running )
			{
				execute();
			}
		}

		if ( stopService )
			stopSelf();
	}

	private boolean isScheduled()
	{
		// TODO Auto-generated method stub: ajaj! az alarmot nem lehet lekérdezni
		return false;
	}

	private void schedule()
	{
		// FIXME handle intent on the broadcast receiver
		final Intent i = new Intent(getBaseContext(), RSSServiceStartReceiver.class);
		i.setAction(ACTION_SYNCH_NOW);
		final PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), 0, i, 0);

		final long nextTime = System.currentTimeMillis() + frequencyInMillis;

		final AlarmManager alarm = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, nextTime, pi);
	}

	private void clearSchedule()
	{
		final Intent i = new Intent(getBaseContext(), RSSServiceStartReceiver.class);
		i.setAction(ACTION_SYNCH_NOW);
		final PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), 0, i, 0);

		final AlarmManager alarm = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pi);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		debugNotification(NOTIFICATION_SERVICE_LIFECYCLE, "Service onDestroy() called.");
	}

	@Override
	public IBinder onBind( final Intent intent )
	{
		debugNotification(NOTIFICATION_SERVICE_LIFECYCLE, "Service onBind() called.");
		return null;
	}

	private void execute()
	{
		new Thread(this).start();
	}

	@Override
	public void run()
	{
		running = true;
		try
		{
			//do a refresh
			synch(feedChanged);
			feedChanged = false;

			//FIXME reschedule

			stopSelf();
		}
		finally
		{
			running = false;
		}
	}

	/**
	 * This method does the actual synch from the stream. It is also handling notifications and 
	 * able to clean up the dB before refresh (this may be necessary, if the feed is changed).
	 * @param cleanUpDB if true, the all cahced item will be deleted before the refresh.
	 */
	private synchronized void synch( final boolean cleanUpDB )
	{
		try
		{
			handler.sendMessage(handler.obtainMessage(CMD_SHOW_REFRESH));
			if ( cleanUpDB )
				RSSStream.deleteItems(getApplicationContext());
			final boolean newItems = RSSStream.synchronize(getBaseContext(), feedUrl);
			if ( newItems
					&& PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean(PREF_NOTIFICATION,
							DEFAULT_NOTIFICATION) )
			{
				handler.sendMessage(handler.obtainMessage(CMD_SHOW_NEWITEM));
			}

			debugNotification(NOTIFICATION_THREAD_LIFECYCLE, "Synch item found: " + newItems);
		}
		catch ( final Exception e )
		{
			Log.e(TAG, "Unable to synchronise the feed: " + feed, e);
			debugNotification(NOTIFICATION_ERRORS, "Exception: " + e);
		}
		finally
		{
			handler.sendMessage(handler.obtainMessage(CMD_HIDE_REFRESH));
		}
	}

	private void debugNotification( final int level, final String message )
	{
		if ( !PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean(PREF_DEBUG, DEFAULT_DEBUG) )
			return;

		Log.d(TAG, message);

		final Context context = getApplicationContext();
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
}
