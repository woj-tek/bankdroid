package hu.androidportal;

import hu.androidportal.rss.RSSStream;

import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Test extends Activity implements OnClickListener, Codes
{

	public static final String TEST_FEED = "http://feeds.feedburner.com/magyarandroidportalblogok";

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test);

		( (Button) findViewById(R.id.deleteLast) ).setOnClickListener(this);
		( (Button) findViewById(R.id.synchDatabase) ).setOnClickListener(this);
		( (Button) findViewById(R.id.testRSSProvider) ).setOnClickListener(this);
		( (Button) findViewById(R.id.testFullList) ).setOnClickListener(this);
		( (Button) findViewById(R.id.cleanDatabase) ).setOnClickListener(this);
	}

	@Override
	public void onClick( final View view )
	{
		if ( view.getId() == R.id.deleteLast )
		{
			try
			{
				RSSStream.deleteLast(getBaseContext());
			}
			catch ( final Exception e )
			{
				Log.d(TAG, "Failed to get the RSS stream", e);
				error(e);
			}
		}
		else if ( view.getId() == R.id.synchDatabase )
		{
			try
			{
				final boolean newArrived = RSSStream.synchronize(getBaseContext(), new URL(TEST_FEED));
				message(newArrived ? "Új bejegyzés érkezet..." : "Nincs új bejegyzés.");
			}
			catch ( final Exception e )
			{
				Log.d(TAG, "Failed to get the RSS stream", e);
				error(e);
			}
		}
		else if ( view.getId() == R.id.testRSSProvider )
		{
			try
			{
				RSSStream.readAndStoreContent(getApplicationContext(), new URL(TEST_FEED));
			}
			catch ( final Exception e )
			{
				Log.d(TAG, "Failed to store stream in DB", e);
				error(e);
			}
		}
		else if ( view.getId() == R.id.cleanDatabase )
		{
			try
			{
				RSSStream.deleteItems(getBaseContext());
			}
			catch ( final Exception e )
			{
				Log.d(TAG, "Failed to clean stream in DB", e);
				error(e);
			}
		}
		else if ( view.getId() == R.id.testFullList )
		{
			startActivity(new Intent(getBaseContext(), ItemListActivity.class));
		}
	}

	private void error( final Exception e )
	{
		final Toast toast = Toast.makeText(getApplicationContext(), "** " + e, Toast.LENGTH_LONG);
		toast.show();
	}

	private void message( final String msg )
	{
		final Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
		toast.show();
	}
}
