package hu.androidportal;

import hu.androidportal.rss.Formatters;
import hu.androidportal.rss.RSSItem;
import hu.androidportal.rss.RSSObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * @author Gabe
 */
public class ItemListActivity extends Activity implements OnItemClickListener, Codes, OnClickListener
{

	protected static final int HANDLER_REFRESH = 812;
	protected static final int HANDLER_STOP = 813;
	protected static final int HANDLER_START = 813;
	protected static final long HANDLER_FREQUENCY = 63000;

	private SimpleCursorAdapter adapter;
	private long lastSynch = -2;
	private Handler handler;

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
						RSSItem.F_PUBDATE }, null, null, RSSItem.F_PUBDATE + " DESC");

		startManagingCursor(cursor);

		final String[] columns = new String[] { RSSObject.F_TITLE, RSSObject.F_SUMMARY, RSSItem.F_AUTHOR };
		final int[] names = new int[] { R.id.titleView, R.id.itemBody, R.id.authorView };

		final int authorIndex = cursor.getColumnIndex(RSSItem.F_AUTHOR);
		final int pubDateIndex = cursor.getColumnIndex(RSSItem.F_PUBDATE);
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
				return false;
			}
		});

		final ListView list = (ListView) findViewById(R.id.rssItemList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		findViewById(R.id.droidLogo).setOnClickListener(this);
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
			builder.append(" perccel ezelõtt");
		}
		else if ( diff >= 0 && diff < 24 * 60 * 60 * 1000L )
		{//within a day
			builder.append(diff / 3600000L);
			builder.append(" órával ezelõtt");
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

		final Context context = getBaseContext();
		RSSSyncService.schedule(context, null);

		//remove notification intent
		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_NEWITEM);

		setLastSuccesfulSynch();

		//FIXME test the refresh
		if ( handler == null )
		{
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

						final Message newMsg = obtainMessage(HANDLER_REFRESH);
						this.sendMessageAtTime(newMsg, System.currentTimeMillis() + HANDLER_FREQUENCY);
					}
					else if ( msg.what == HANDLER_STOP )
					{
						running = false;
					}
					else if ( msg.what == HANDLER_START )
					{
						running = true;
					}
				}
			};
		}
		else
		{
			final Message msg = handler.obtainMessage(HANDLER_START);
			handler.sendMessage(msg);
		}

		final Message msg = handler.obtainMessage(HANDLER_REFRESH);
		handler.sendMessageAtTime(msg, System.currentTimeMillis() + HANDLER_FREQUENCY);
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
		if ( lastSuccesfulSynch == lastSynch )
		{
			//no change 
			return;
		}

		lastSynch = lastSuccesfulSynch;

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

	@Override
	public void onClick( final View view )
	{
		if ( view.getId() == R.id.droidLogo )
		{
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_ANDROIDPORTAL_HU)));
		}
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.menuRefresh )
		{
			final Intent intent = new Intent(ACTION_MANUAL_START);
			intent.setClass(getBaseContext(), RSSSyncService.class);
			startService(intent);
		}
		else if ( item.getItemId() == R.id.menuPref )
		{
			startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
		}
		else if ( item.getItemId() == R.id.menuAbout )
		{
			startActivity(new Intent(getBaseContext(), AboutActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu( final Menu menu )
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}
}
