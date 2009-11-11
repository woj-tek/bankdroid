package bankdroid.soda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity implements Codes, OnClickListener
{
	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		Eula.show(this);

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
			final Message message = BankManager.getLastMessage(getApplicationContext());
			if ( message == null )
			{
				final Toast toast = Toast.makeText(getApplicationContext(), R.string.noMessageYet, Toast.LENGTH_SHORT);
				toast.show();
			}
			else
			{
				final Intent intent = new Intent(getBaseContext(), SMSOTPDisplay.class);
				intent.setAction(ACTION_REDISPLAY);
				intent.putExtra(BANKDROID_SODA_SMSMESSAGE, message.message);
				intent.putExtra(BANKDROID_SODA_BANK, message.bank);
				intent.putExtra(BANKDROID_SODA_SMSCODE, message.bank.extractCode(message.message));
				intent.putExtra(BANKDROID_SODA_SMSTIMESTAMP, message.timestamp);

				startActivity(intent);
			}
		}
		else if ( v.getId() == R.id.about )
		{
			final Intent aboutIntent = new Intent(getBaseContext(), AboutActivity.class);
			startActivity(aboutIntent);
			//getSharedPreferences(Eula.PREFERENCES_EULA, Activity.MODE_PRIVATE).edit().putBoolean(
			//		Eula.PREFERENCE_EULA_ACCEPTED, false).commit();// use this line for eula testing
		}
		else if ( v.getId() == R.id.preferences )
		{
			final Intent settingsActivity = new Intent(getBaseContext(), Preferences.class);
			startActivity(settingsActivity);
		}
		else if ( v.getId() == R.id.help )
		{
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_INFO_SITE));
			startActivity(viewIntent);
		}
		else if ( v.getId() == R.id.submitSample )
		{
			final Intent submitIntent = new Intent(getBaseContext(), SMSListActivity.class);
			startActivity(submitIntent);
		}
	}

}
