package hu.androidportal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity implements Codes, OnClickListener
{
	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.about);

		findViewById(R.id.marketSearch).setOnClickListener(this);
		findViewById(R.id.url1).setOnClickListener(this);
		findViewById(R.id.url2).setOnClickListener(this);
		findViewById(R.id.url3).setOnClickListener(this);
	}

	@Override
	public void onClick( final View src )
	{
		if ( src.getId() == R.id.marketSearch )
		{
			try
			{
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=Bankdroid")));
			}
			catch ( final Exception e )
			{
				Log.e(TAG, "Nem sikerült megnyitni a piacot.", e);
				final Toast toast = Toast.makeText(getBaseContext(), "Nem sikerült megnyitni a Marketet.",
						Toast.LENGTH_LONG);
				toast.show();
			}
		}
		else if ( src instanceof TextView )
		{
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(( (TextView) src ).getText().toString())));
		}
	}

}
