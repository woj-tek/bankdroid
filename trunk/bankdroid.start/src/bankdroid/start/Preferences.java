package bankdroid.start;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * @author Gabe
 */
public class Preferences extends PreferenceActivity implements Codes
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final Editor editor = preferences.edit();
		if ( !preferences.getBoolean(PREF_SAVE_LAST_LOGIN, true) )
		{
			editor.remove(PREF_LAST_LOGINID);
			editor.remove(PREF_LAST_BANK);
		}
		if ( !preferences.getBoolean(PREF_SAVE_PASSWORD, false) )
		{
			editor.remove(PREF_LAST_PASSWORD);
		}
		editor.commit();
	}

}
