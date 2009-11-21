package hu.androidportal;

import hu.androidportal.rss.RSSChannel;
import hu.androidportal.rss.RSSObject;
import hu.androidportal.rss.RSSStream;

import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Test extends Activity implements OnClickListener, Codes
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test);

		( (Button) findViewById(R.id.testRSSAPI) ).setOnClickListener(this);
		( (Button) findViewById(R.id.testRSSProvider) ).setOnClickListener(this);
		( (Button) findViewById(R.id.testFullList) ).setOnClickListener(this);
		( (Button) findViewById(R.id.cleanDatabase) ).setOnClickListener(this);
	}

	@Override
	public void onClick( final View view )
	{
		if ( view.getId() == R.id.testRSSAPI )
		{
			try
			{
				final RSSChannel channel = RSSStream.readChannelContent(new URL(
						"http://feeds.feedburner.com/magyarandroidportalblogok"));
				Log.d(TAG, channel.toString());
				for ( final RSSObject item : channel.items )
				{
					Log.d(TAG, item.toString());
				}
			}
			catch ( final Exception e )
			{
				Log.d(TAG, "Failed to get the RSS stream", e);
			}
		}
		else if ( view.getId() == R.id.testRSSProvider )
		{
			try
			{
				RSSStream.readAndStoreContent(getApplicationContext(), new URL(
						"http://feeds.feedburner.com/magyarandroidportalblogok"));
			}
			catch ( final Exception e )
			{
				Log.d(TAG, "Failed to store stream in DB", e);
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
			}
		}
		else if ( view.getId() == R.id.testFullList )
		{
			startActivity(new Intent(getBaseContext(), ItemListActivity.class));
		}
	}
}
