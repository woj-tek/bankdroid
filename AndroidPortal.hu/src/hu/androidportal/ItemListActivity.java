package hu.androidportal;

import hu.androidportal.rss.Formatters;
import hu.androidportal.rss.RSSItem;
import hu.androidportal.rss.RSSObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class ItemListActivity extends Activity implements OnItemClickListener, Codes, OnClickListener
{

	private SimpleCursorAdapter adapter;

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

		( (TextView) findViewById(R.id.appName) ).setOnClickListener(this);
	}

	@Override
	public void onItemClick( final AdapterView<?> adapter, final View view, final int position, final long id )
	{
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.withAppendedPath(RSSItem.CONTENT_URI, String.valueOf(id)));
		intent.setClass(getBaseContext(), ItemViewActivity.class);
		startActivity(intent);
	}

	static CharSequence getAuthorText( final String author, final String date )
	{
		final StringBuilder builder = new StringBuilder(author);
		try
		{
			final Date pubDate = Formatters.getTimstampFormat().parse(date);

			builder.append(", ");
			final long diff = Calendar.getInstance().getTime().getTime() - pubDate.getTime();
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
				builder.append(Formatters.getShortDateFormat().format(pubDate));
			}
		}
		catch ( final ParseException e )
		{
			Log.d(TAG, "Failed to parse date: " + e);
		}
		return builder;
	}

	@Override
	public void onClick( final View view )
	{
		if ( view.getId() == R.id.appName )
		{
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_ANDROIDPORTAL_HU)));
		}
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.menuRefresh )
		{
			//FIXME refresh menu
		}
		else if ( item.getItemId() == R.id.menuPref )
		{
			startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
		}
		else if ( item.getItemId() == R.id.menuAbout )
		{
			//FIXME about menu
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
