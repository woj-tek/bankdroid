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
 * TODO handle prefernces for frequency, and feed url
 * TODO listen for internet connection to suspend/resume synch
 * TODO listen for phone start up to start synch
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

	//FIXME set frequency changed somewhere
	private boolean frequencyChanged = false;

	@Override
	public void onCreate()
	{
		super.onCreate();

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
				}
				else if ( msg.what == CMD_SHOW_NEWITEM )
				{
					//FIXME show new icon in the notification bar
				}
			}
		};

		//create a thread to do the real background work
		if ( background != null )
			throw new IllegalStateException("Invalid object state. Background thread already created.");
		run = false;
		background = new Thread(this);

		//initialize preferences
		updateFrequency();

		updateFeed();
	}

	/**
	 * Updates the feed url from preferences.
	 * @return True, if the current and the new urls are not the same.
	 */
	private boolean updateFeed()
	{
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final String feed = preferences.getString(PREF_FEED, DEFAULT_FEED);

		if ( !feed.equals(this.feed) )
			return false;

		try
		{
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
	 * Updates the frequency from preferences.
	 * @return True, if the current and the new frequency are not the same.
	 */
	private boolean updateFrequency()
	{
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final String frequency = preferences.getString(PREF_FREQUENCY, DEFAULT_FREQUENCY);

		if ( !frequency.equals(this.frequency) )
			return false;

		this.frequency = frequency;
		frequencyInMillis = Long.parseLong(frequency) * 60 * 1000;

		return true;
	}

	@Override
	public void onStart( final Intent intent, final int startId )
	{
		super.onStart(intent, startId);

		//FIXME handle preferences changes somewhere (problem could be here that the new preferences are not yet set
		if ( !background.isAlive() )
		{
			run = true;
			background.start();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		run = false;
		if ( background.isAlive() )
			background.interrupt();
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
				try
				{
					RSSStream.synchronize(getBaseContext(), feedUrl);
				}
				catch ( final Exception e )
				{
					Log.e(TAG, "Unable to synchronise the feed: " + feed, e);
				}
			}

			//wait
			try
			{
				Thread.sleep(frequencyInMillis);
			}
			catch ( final Exception e )
			{
				//do nothing. If necessary the thread will be stoped on the "run" variable.
			}
		}
	}
}
