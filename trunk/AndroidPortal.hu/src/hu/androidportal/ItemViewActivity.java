package hu.androidportal;

import hu.androidportal.rss.RSSItem;
import hu.androidportal.rss.RSSObject;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

public class ItemViewActivity extends Activity implements Codes, OnClickListener
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

		( (TextView) findViewById(R.id.appName) ).setOnClickListener(this);
		( (TextView) findViewById(R.id.titleText) ).setOnClickListener(this);
		( (TextView) findViewById(R.id.author) ).setOnClickListener(this);
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

		Log.d(TAG, "Selected item is going to be displayed: " + uriToDisplay);

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

		//create cursor for the view
		final Cursor cursor = getContentResolver().query(
				uriToDisplay,
				new String[] { RSSObject.F__ID, RSSObject.F_TITLE, RSSObject.F_DESCRIPTION, RSSItem.F_AUTHOR,
						RSSItem.F_PUBDATE, RSSObject.F_LINK }, null, null, RSSItem.DEFAULT_SORT_ORDER);

		if ( cursor.moveToFirst() )
		{
			final String description = cursor.getString(cursor.getColumnIndex(RSSItem.F_DESCRIPTION));
			( (WebView) findViewById(R.id.webView) ).loadDataWithBaseURL("http://androidportal.hu", description,
					"text/html", "utf-8", "http://androidportal.hu");
			( (TextView) findViewById(R.id.titleText) ).setText(cursor
					.getString(cursor.getColumnIndex(RSSItem.F_TITLE)));
			( (TextView) findViewById(R.id.author) ).setText(ItemListActivity.getAuthorText(cursor.getString(cursor
					.getColumnIndex(RSSItem.F_AUTHOR)), cursor.getString(cursor.getColumnIndex(RSSItem.F_PUBDATE))));

			url = cursor.getString(cursor.getColumnIndex(RSSObject.F_LINK));
		}
		else
		{
			Log.w(TAG, "Item is not found in DB: " + uriToDisplay);
		}
	}

	@Override
	public void onClick( final View view )
	{
		if ( view.getId() == R.id.appName )
		{
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_ANDROIDPORTAL_HU)));
		}
		if ( view.getId() == R.id.titleView || view.getId() == R.id.author )
		{
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}
	}

}
