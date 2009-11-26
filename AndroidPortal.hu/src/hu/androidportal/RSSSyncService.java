package hu.androidportal;

import hu.androidportal.rss.RSSStream;

import java.net.URL;

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
 * FIXME implement preferences change starts
 * 
 * TODO listen for internet connection to suspend/resume synch
 * TODO listen for phone start up to start synch
 * TODO handle manual refresh
 * 
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

	private boolean run;
	private Thread background;

	private String feed;
	private URL feedUrl;
	private String frequency;
	private long frequencyInMillis;

	private boolean frequencyChanged = false;
	private boolean feedChanged = false;

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d(TAG, "Service onCreate called.");

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
				if ( msg.what == CMD_SHOW_REFRESH )
				{
					//FIXME show refresh icon in notification bar
					Log.d(TAG, "Show refresh icon.");
				}
				else if ( msg.what == CMD_HIDE_REFRESH )
				{
					//FIXME hide refresh icon in notification bar
					Log.d(TAG, "HIDE refresh icon.");
				}
				else if ( msg.what == CMD_SHOW_NEWITEM )
				{
					//FIXME show new icon in the notification bar
					Log.d(TAG, "Show NEW ITEM icon.");
				}
			}
		};

		//create a thread to do the real background work
		if ( background != null )
			throw new IllegalStateException("Invalid object state. Background thread already created.");
		run = false;
		background = new Thread(this);

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

		Log.d(TAG, "Service onStart called.");

		if ( intent.getAction().equals(ACTION_MANUAL_START) )
		{
			//start synch immediately in a seperate thread.
			new Thread(new Runnable()
			{
				public void run()
				{
					synch(false);
				}
			}).start();
		}
		else
		{
			if ( intent.getAction().equals(ACTION_FEED_CHANGED) )
			{
				updateFeed(intent.getStringExtra(PREF_FEED));
				feedChanged = true;
				//clean up database and start synch immediately in a seperate thread.
				notifyAll();
			}
			else if ( intent.getAction().equals(ACTION_FREQ_CHANGED) )
			{
				updateFeed(intent.getStringExtra(PREF_FREQUENCY));
				frequencyChanged = true;
				notifyAll(); //inform the thread that something happened.
			}

			if ( frequencyInMillis > 0 && !background.isAlive() )
			{
				run = true;
				background.start();
			}
			else if ( frequencyInMillis == 0 )
			{
				stopSelf();
			}
		}

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		run = false;
		if ( background.isAlive() )
			notifyAll();
	}

	@Override
	public IBinder onBind( final Intent intent )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run()
	{
		while ( run )
		{
			if ( frequencyChanged )
			{
				//do nothing, no refresh is required but only after the first round
				frequencyChanged = false;
			}
			else
			{
				//do a refresh
				synch(feedChanged);
				feedChanged = false;
			}

			//wait
			try
			{
				wait(frequencyInMillis);
			}
			catch ( final InterruptedException e )
			{
				// do nothing, thread will stop, if run flag changes.
			}
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
		}
		catch ( final Exception e )
		{
			Log.e(TAG, "Unable to synchronise the feed: " + feed, e);
		}
		finally
		{
			handler.sendMessage(handler.obtainMessage(CMD_HIDE_REFRESH));
		}
	}
}
