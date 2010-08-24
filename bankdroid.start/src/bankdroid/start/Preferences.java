package bankdroid.start;

import android.os.Bundle;
import android.preference.PreferenceActivity;

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

}
