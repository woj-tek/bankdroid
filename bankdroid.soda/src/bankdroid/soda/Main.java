package bankdroid.soda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements Codes, OnClickListener
{
	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		( (Button) findViewById(R.id.viewLast) ).setOnClickListener(this);
		( (Button) findViewById(R.id.manageBanks) ).setOnClickListener(this);
		( (Button) findViewById(R.id.submitSample) ).setOnClickListener(this);
		( (Button) findViewById(R.id.preferences) ).setOnClickListener(this);
		( (Button) findViewById(R.id.help) ).setOnClickListener(this);
		( (Button) findViewById(R.id.about) ).setOnClickListener(this);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.manageBanks )
		{
			final Intent bankListIntent = new Intent(getBaseContext(), BankListActivity.class);
			startActivity(bankListIntent);
		}
		else if ( v.getId() == R.id.viewLast )
		{
			//TODO handle viewLast
			Bank.getAllBanks(getBaseContext());
		}
		else if ( v.getId() == R.id.about )
		{
			final Intent aboutIntent = new Intent(getBaseContext(), AboutActivity.class);
			startActivity(aboutIntent);
		}
		else if ( v.getId() == R.id.preferences )
		{
			final Intent settingsActivity = new Intent(getBaseContext(), Preferences.class);
			startActivity(settingsActivity);
		}
		else if ( v.getId() == R.id.help )
		{
			final Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(INFO_SITE_URL));
			startActivity(viewIntent);
		}
		else if ( v.getId() == R.id.submitSample )
		{
			final Intent submitIntent = new Intent(getBaseContext(), SMSListActivity.class);
			startActivity(submitIntent);

			//TODO display thank you message for submission
		}
	}

}