package hu.tminfo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;
import bankdroid.rss.RSSItem;
import bankdroid.rss.RSSObject;
import bankdroid.rss.RSSStream;

/**
 * @author gyenes
 */
public class ItemViewActivity extends ToolbarActivity implements Codes, OnClickListener
{
	private final static String STORE_URI = "STORE_URI";

	private Uri uriToDisplay = null;
	private String url = null;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.viewitem);

		if ( savedInstanceState != null && savedInstanceState.containsKey(STORE_URI) )
			uriToDisplay = Uri.parse(savedInstanceState.getString(STORE_URI));

		findViewById(R.id.titleText).setOnClickListener(this);
		findViewById(R.id.author).setOnClickListener(this);
		findViewById(R.id.channelTags).setOnClickListener(this);

		findViewById(R.id.toolbarShare).setOnClickListener(this);
		findViewById(R.id.toolbarPreferences).setOnClickListener(this);
		findViewById(R.id.toolbarAbout).setOnClickListener(this);

		final WebView web = (WebView) findViewById(R.id.webView);
		web.getSettings().setBuiltInZoomControls(true);
	}

	@Override
	protected void onRestoreInstanceState( final Bundle savedInstanceState )
	{
		super.onRestoreInstanceState(savedInstanceState);
		if ( savedInstanceState != null && savedInstanceState.containsKey(STORE_URI) )
			uriToDisplay = Uri.parse(savedInstanceState.getString(STORE_URI));
	}

	@Override
	protected void onSaveInstanceState( final Bundle outState )
	{
		super.onSaveInstanceState(outState);
		if ( uriToDisplay != null )
		{
			outState.putSerializable(STORE_URI, uriToDisplay.toString());
		}
	}

	@Override
	protected void onNewIntent( final Intent intent )
	{
		super.onNewIntent(intent);
		Log.d(TAG, "Intent received with: " + intent.getData());
		setIntent(intent);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if ( uriToDisplay == null )
		{
			final Intent intent = getIntent();

			if ( intent != null && Intent.ACTION_VIEW.equals(intent.getAction()) )
			{
				uriToDisplay = intent.getData();
			}
			else
			{
				Log.w(TAG, "No data or invalid intent is received:" + intent);
				return;
			}
		}

		Log.d(TAG, "Selected item is going to be displayed: " + uriToDisplay);

		//create cursor for the view
		final Cursor cursor = getContentResolver().query(
				uriToDisplay,
				new String[] { RSSObject.F__ID, RSSObject.F_TITLE, RSSObject.F_DESCRIPTION, RSSItem.F_AUTHOR,
						RSSItem.F_PUBDATE, RSSObject.F_LINK, RSSItem.F_STATUS, RSSItem.F_CHANNELS }, null, null,
				RSSItem.DEFAULT_SORT_ORDER);

		if ( cursor.moveToFirst() )
		{
			final String description = cursor.getString(cursor.getColumnIndex(RSSItem.F_DESCRIPTION));

			//FIXME it may not work for non-local feeds.
			final String baseUrl = getString(R.string.url);
			( (WebView) findViewById(R.id.webView) ).loadDataWithBaseURL(baseUrl, description, "text/html", "utf-8",
					baseUrl);
			( (TextView) findViewById(R.id.titleText) ).setText(cursor
					.getString(cursor.getColumnIndex(RSSItem.F_TITLE)));
			( (TextView) findViewById(R.id.author) ).setText(ItemListActivity.getAuthorText(cursor.getString(cursor
					.getColumnIndex(RSSItem.F_AUTHOR)), cursor.getString(cursor.getColumnIndex(RSSItem.F_PUBDATE))));
			( (TextView) findViewById(R.id.channelTags) ).setText(cursor.getString(cursor
					.getColumnIndex(RSSItem.F_CHANNELS)));

			url = cursor.getString(cursor.getColumnIndex(RSSObject.F_LINK));

			//update status to read if necessary
			final int status = cursor.getInt(cursor.getColumnIndex(RSSItem.F_STATUS));
			if ( status == RSSItem.STATUS_UNREAD )
			{
				final Thread thread = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						RSSStream.setStatus(getApplicationContext(), uriToDisplay, RSSItem.STATUS_READ);
					}
				});
				thread.start();
			}
		}
		else
		{
			Log.w(TAG, "Item is not found in DB: " + uriToDisplay);
		}

		cursor.close();
	}

	@Override
	public void onClick( final View view )
	{
		if ( view.getId() == R.id.titleText || view.getId() == R.id.channelTags || view.getId() == R.id.author )
		{
			trackClickEvent(ACTION_BROWSE, "rssItem");

			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}
		else if ( view.getId() == R.id.toolbarShare )
		{
			shareItem();
		}
		else if ( view.getId() == R.id.toolbarPreferences )
		{
			preferences();
		}
		else if ( view.getId() == R.id.toolbarAbout )
		{
			about();
		}
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.menuShare )
		{
			shareItem();
		}
		else if ( item.getItemId() == R.id.menuPref )
		{
			preferences();
		}
		else if ( item.getItemId() == R.id.menuAbout )
		{
			about();
		}
		return super.onOptionsItemSelected(item);
	}

	private void shareItem()
	{
		trackClickEvent(ACTION_SEND, "shareRssItem");

		final Intent send = new Intent(Intent.ACTION_SEND);
		send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareTitle));
		send.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareBody) + url);
		send.setType("text/plain");
		startActivity(Intent.createChooser(send, "Válassz alkalmazást:"));
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

	//FIXME handle toolbar events
	@Override
	public boolean onCreateOptionsMenu( final Menu menu )
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.itemmenu, menu);
		return true;
	}

}
