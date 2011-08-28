package bankdroid.smskey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;

/**
 * @author Gabe
 */
public class Preferences extends PreferenceActivity implements Codes, OnPreferenceChangeListener
{
	private final static int DISPLAY_TOAST = 354;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);

		final Preference resetDb = findPreference(PREF_RESET_DB);
		resetDb.setOnPreferenceChangeListener(this);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if ( Build.VERSION.SDK_INT < 5 )
		{
			final Preference unlockScreen = findPreference(PREF_UNLOCK_SCREEN);
			unlockScreen.setEnabled(false);
			unlockScreen.setSelectable(false);
			unlockScreen.setDefaultValue(Boolean.FALSE);
			final Editor editor = preferences.edit();
			editor.putBoolean(PREF_UNLOCK_SCREEN, false);
			editor.commit();
		}
		final int count = preferences.getInt(PREF_CODE_COUNT, 0);
		final Preference codeCount = findPreference(PREF_CODE_COUNT);
		codeCount.setSummary(String.valueOf(count));
	}

	@Override
	public boolean onPreferenceChange( final Preference pref, final Object newValue )
	{
		if ( pref.getKey().equals(PREF_RESET_DB) && ( (Boolean) newValue ) )
		{
			showDialog(0);
		}
		return true;
	}

	private void resetDb( final Preference pref )
	{
		final Handler handler = new Handler()
		{
			@Override
			public void handleMessage( final Message msg )
			{
				super.handleMessage(msg);
				if ( msg.what == DISPLAY_TOAST )
				{
					final Toast toast = Toast.makeText(getBaseContext(), R.string.msgDbReset, Toast.LENGTH_SHORT);
					toast.show();
					( (CheckBoxPreference) pref ).setChecked(false);
				}
			}
		};

		( new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				BankProvider.resetDb(getBaseContext());
				handler.sendMessage(handler.obtainMessage(DISPLAY_TOAST));
			}
		}) ).start();
	}

	@Override
	protected Dialog onCreateDialog( final int id )
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.msgAreYouSure)).setCancelable(false)
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick( final DialogInterface dialog, final int id )
					{
						final Preference pref = findPreference(PREF_RESET_DB);
						resetDb(pref);
						dialog.dismiss();
					}
				}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick( final DialogInterface dialog, final int id )
					{
						final CheckBoxPreference pref = (CheckBoxPreference) findPreference(PREF_RESET_DB);
						pref.setChecked(false);
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		return alert;
	}

}
