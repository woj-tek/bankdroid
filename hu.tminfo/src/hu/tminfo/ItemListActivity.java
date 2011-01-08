package hu.tminfo;

import hu.tminfo.RSSSyncService.RefreshListener;
import hu.tminfo.widget.PortalWidgetProvider;
import hu.tminfo.widget.Widget11Provider;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;
import bankdroid.rss.Formatters;
import bankdroid.rss.RSSItem;
import bankdroid.rss.RSSObject;
import bankdroid.rss.RSSStream;

/**
 * @author Gabe
 */
public class ItemListActivity extends ToolbarActivity implements OnItemClickListener, Codes, OnClickListener,
		RefreshListener
{

	protected static final int HANDLER_REFRESH = 812;
	protected static final int HANDLER_STOP = 813;
	protected static final int HANDLER_START = 814;
	protected static final int HANDLER_SHOW_INIT_MESSAGE = 815;
	protected static final int HANDLER_CHANGE_TOOLBAR_ICON = 816;

	protected static final String CHANNEL_TAG = "CHANNEL_TAG";

	protected static final long HANDLER_FREQUENCY = 5000;

	private SimpleCursorAdapter adapter;
	private Handler handler;

	private RSSSyncService synchService;
	private boolean isServiceBound;
	private boolean isRefreshRunning;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.rsslist);

		//create cursor for the view
		final Cursor cursor = getContentResolver().query(
				RSSItem.CONTENT_URI,
				new String[] { RSSObject.F__ID, RSSObject.F_TITLE, RSSObject.F_SUMMARY, RSSItem.F_AUTHOR,
						RSSItem.F_PUBDATE, RSSItem.F_STATUS, RSSItem.F_CHANNELS }, null, null,
				RSSItem.F_PUBDATE + " DESC");

		startManagingCursor(cursor);

		final String[] columns = new String[] { RSSObject.F_TITLE, RSSObject.F_SUMMARY, RSSItem.F_AUTHOR,
				RSSItem.F_STATUS, RSSItem.F_CHANNELS };
		final int[] names = new int[] { R.id.titleView, R.id.itemBody, R.id.authorView, R.id.readIndicator,
				R.id.channelTags };

		final int authorIndex = cursor.getColumnIndex(RSSItem.F_AUTHOR);
		final int pubDateIndex = cursor.getColumnIndex(RSSItem.F_PUBDATE);
		final int statusIndex = cursor.getColumnIndex(RSSItem.F_STATUS);
		adapter = new SimpleCursorAdapter(this, R.layout.rsslistitem, cursor, columns, names);
		adapter.setViewBinder(new ViewBinder()
		{

			@Override
			public boolean setViewValue( final View view, final Cursor cursor, final int columnIndex )
			{
				if ( columnIndex == authorIndex )
				{
					( (TextView) view ).setText(getAuthorText(cursor.getString(authorIndex), cursor
							.getString(pubDateIndex)));
					return true;
				}
				else if ( columnIndex == statusIndex )
				{
					//set read / unread indicator
					if ( cursor.getInt(statusIndex) == RSSItem.STATUS_UNREAD )
					{
						view.setBackgroundDrawable(getResources().getDrawable(R.drawable.undread_indicator));
					}
					else
					{
						view.setBackgroundDrawable(null);
					}
					return true;
				}
				return false;
			}
		});

		final ListView list = (ListView) findViewById(R.id.rssItemList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		findViewById(R.id.droidLogo).setOnClickListener(this);

		handler = new Handler()
		{
			private boolean running = true;

			@Override
			public void handleMessage( final Message msg )
			{
				super.handleMessage(msg);

				if ( msg.what == HANDLER_REFRESH && running )
				{
					setLastSuccesfulSynch();
					clearNotification();
					schedule();
					//Log.d(TAG, "**** Handler refreshed.");
				}
				else if ( msg.what == HANDLER_STOP )
				{
					running = false;
					//Log.d(TAG, "**** Handler stoped.");
				}
				else if ( msg.what == HANDLER_START )
				{
					running = true;
					schedule();
					//Log.d(TAG, "**** Handler started.");
				}
				else if ( msg.what == HANDLER_SHOW_INIT_MESSAGE )
				{
					final String channelTag = msg.getData().getString(CHANNEL_TAG);
					final Toast toast = Toast.makeText(getBaseContext(), "Alapértelmezetten a " + channelTag
							+ " elemei jellenek meg. Ezt átállíthatod a beállítások menüben.", Toast.LENGTH_LONG);
					toast.show();
				}
				else if ( msg.what == HANDLER_CHANGE_TOOLBAR_ICON )
				{
					//change toolbar icon here
					( (ImageButton) findViewById(R.id.toolbarRefresh) )
							.setImageResource(isRefreshRunning ? android.R.drawable.ic_menu_close_clear_cancel
									: android.R.drawable.ic_menu_rotate);
				}
			}

			private void schedule()
			{
				final Message newMsg = obtainMessage(HANDLER_REFRESH);
				if ( !sendMessageDelayed(newMsg, HANDLER_FREQUENCY) )
					Log.e(TAG, "**** Failed to initialize the loop.");
			}
		};

		findViewById(R.id.toolbarPreferences).setOnClickListener(this);
		findViewById(R.id.toolbarRefresh).setOnClickListener(this);
		findViewById(R.id.toolbarReadAll).setOnClickListener(this);
		findViewById(R.id.toolbarAbout).setOnClickListener(this);
	}

	@Override
	public void onItemClick( final AdapterView<?> adapter, final View view, final int position, final long id )
	{
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.withAppendedPath(RSSItem.CONTENT_URI, String.valueOf(id)));
		intent.setClass(getBaseContext(), ItemViewActivity.class);
		startActivity(intent);
	}

	public static CharSequence getAuthorText( final String author, final String date )
	{
		try
		{
			return getAuthorText(author, Formatters.getTimstampFormat().parse(date));
		}
		catch ( final ParseException e )
		{
			Log.d(TAG, "Failed to parse date: " + e);
		}
		return author;
	}

	public static CharSequence getAuthorText( final String author, final Date date )
	{
		final StringBuilder builder = new StringBuilder(author);

		if ( builder.length() > 0 )
			builder.append(", ");

		final long diff = Calendar.getInstance().getTime().getTime() - date.getTime();
		if ( diff >= 0 && diff < 60 * 60 * 1000L )
		{//within an hour
			builder.append(diff / 60000L);
			builder.append(" perccel ezelőtt");
		}
		else if ( diff >= 0 && diff < 24 * 60 * 60 * 1000L )
		{//within a day
			builder.append(diff / 3600000L);
			builder.append(" órával ezelőtt");
		}
		else
		{
			builder.append(Formatters.getShortDateFormat().format(date));
		}
		return builder;
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setLastSuccesfulSynch();

		final Thread initThread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				doBindService();

				final Context context = getBaseContext();

				// on clean install there is no feed set.
				//find out whether any channel is set to synch
				final String[] tags = context.getResources().getStringArray(R.array.feedLabels);

				boolean isAnySet = false;
				final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				for ( int i = 0; i < tags.length; i++ )
				{
					final boolean isSet = preferences.getBoolean(PREF_FEED_PREFIX + i, false);
					if ( isSet )
					{
						isAnySet = true;
						break;
					}
				}
				if ( !isAnySet )
				{
					//set the first feed to 0
					final Editor edit = preferences.edit();
					edit.putBoolean(PREF_FEED_PREFIX + 0, true);
					edit.commit();

					final Message msgShowInitMessage = handler.obtainMessage(HANDLER_SHOW_INIT_MESSAGE);
					final Bundle data = msgShowInitMessage.getData();
					data.putString(CHANNEL_TAG, tags[0]);
					msgShowInitMessage.setData(data);
					handler.sendMessage(msgShowInitMessage);
				}

				//schedule refresh if necessary
				RSSSyncService.schedule(context, null);

				clearNotification();
			}
		});
		initThread.start();

		final Message msgStart = handler.obtainMessage(HANDLER_START);
		handler.sendMessage(msgStart);
	}

	private void clearNotification()
	{
		//remove notification intent
		final NotificationManager nm = (NotificationManager) getBaseContext().getSystemService(
				Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_NEWITEM);
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		//stop refreshing the last update text 
		final Message msg = handler.obtainMessage(HANDLER_STOP);
		handler.sendMessage(msg);
	}

	private void setLastSuccesfulSynch()
	{
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final long lastSuccesfulSynch = preferences.getLong(PREF_LAST_SUCCESFUL_SYCNH, -1);

		String textToDisplay = null;
		if ( lastSuccesfulSynch > 0 )
		{
			final CharSequence lastSynchText = getAuthorText("", new Date(lastSuccesfulSynch));
			textToDisplay = "Utolsó frissítés: " + lastSynchText;
		}
		else
		{
			textToDisplay = "Kézi frissítés";
		}

		( (TextView) findViewById(R.id.lastSynch) ).setText(textToDisplay);

	}

	private final ServiceConnection mConnection = new ServiceConnection()
	{
		public void onServiceConnected( final ComponentName className, final IBinder service )
		{
			Log.d(TAG, "ItemListActivity.onServiceConnected()");
			synchService = ( (RSSSyncService.LocalBinder) service ).getService();
			synchService.setRefreshListener(ItemListActivity.this);
			if ( !synchService.isRunning() )
			{
				synchService.clearRefresh();
			}
			else
			{
				onStartRefresh();
			}
		}

		public void onServiceDisconnected( final ComponentName className )
		{
			Log.d(TAG, "ItemListActivity.onServiceDisconnected()");
			synchService = null;
			onStopRefresh();
		}
	};

	void doBindService()
	{
		bindService(new Intent(this, RSSSyncService.class), mConnection, 0);
		isServiceBound = true;
	}

	void doUnbindService()
	{
		if ( isServiceBound )
		{
			// Detach our existing connection.
			unbindService(mConnection);
			isServiceBound = false;
		}
	}

	@Override
	public void onClick( final View view )
	{
		if ( view.getId() == R.id.droidLogo )
		{
			final String url = getString(R.string.url);
			trackClickEvent(ACTION_BROWSE, url);
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}
		else if ( view.getId() == R.id.toolbarAbout )
		{
			about();
		}
		else if ( view.getId() == R.id.toolbarPreferences )
		{
			preferences();
		}
		else if ( view.getId() == R.id.toolbarReadAll )
		{
			markAllRead();
		}
		else if ( view.getId() == R.id.toolbarRefresh )
		{
			refresh();
		}
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.menuRefresh )
		{
			refresh();
		}
		else if ( item.getItemId() == R.id.menuPref )
		{
			preferences();
		}
		else if ( item.getItemId() == R.id.menuAbout )
		{
			about();
		}
		else if ( item.getItemId() == R.id.menuReadAll )
		{
			markAllRead();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		doUnbindService();
		synchService = null;
	}

	private void refresh()
	{
		if ( !isRefreshRunning )
		{
			trackClickEvent(ACTION_CLICK, "ManualRefresh");

			final Toast toast = Toast.makeText(getBaseContext(), "Kézi frissítés.", Toast.LENGTH_SHORT);
			toast.show();

			final Intent intent = new Intent(ACTION_MANUAL_START);
			intent.setClass(this, RSSSyncService.class);
			startService(intent);

			doBindService();
		}
		else
		{
			trackClickEvent(ACTION_CLICK, "ForceClosed");
			stopService(new Intent(ItemListActivity.this, RSSSyncService.class));
			Toast.makeText(getBaseContext(), "Frissítés hamarosan leáll.", Toast.LENGTH_SHORT).show();
		}
	}

	private void preferences()
	{
		trackPage("PreferenceActivity");
		startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
	}

	private void about()
	{
		startActivity(new Intent(getBaseContext(), AboutActivity.class));
	}

	private void markAllRead()
	{
		trackClickEvent(ACTION_CLICK, "ReadAll");

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				RSSStream.markAllAsRead(getBaseContext());

				// Push update for this widget to the home screen
				final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ItemListActivity.this);
				PortalWidgetProvider.updateWidgets(getBaseContext(), appWidgetManager);
				Widget11Provider.updateWidgets(getBaseContext(), appWidgetManager);
			}
		}).start();
		final Toast toast = Toast.makeText(getBaseContext(), "Minden elem olvasottra vált.", Toast.LENGTH_SHORT);
		toast.show();

	}

	@Override
	public boolean onPrepareOptionsMenu( final Menu menu )
	{
		menu.findItem(R.id.menuRefresh).setIcon(
				isRefreshRunning ? android.R.drawable.ic_menu_close_clear_cancel : android.R.drawable.ic_menu_rotate);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu( final Menu menu )
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);

		menu.findItem(R.id.menuRefresh).setIcon(
				isRefreshRunning ? android.R.drawable.ic_menu_close_clear_cancel : android.R.drawable.ic_menu_rotate);

		return true;
	}

	@Override
	public void onStartRefresh()
	{
		if ( !isRefreshRunning )
		{
			isRefreshRunning = true;

			if ( useToolbar )
			{
				handler.sendMessage(handler.obtainMessage(HANDLER_CHANGE_TOOLBAR_ICON));
			}
		}

	}

	@Override
	public void onStopRefresh()
	{
		if ( isRefreshRunning )
		{
			isRefreshRunning = false;
			if ( useToolbar )
			{
				handler.sendMessage(handler.obtainMessage(HANDLER_CHANGE_TOOLBAR_ICON));
			}
		}
	}
}
