package hu.androidportal;

import hu.androidportal.rss.RSSStream;

import java.net.URL;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Gabe
 *
 *FIXME set frequency text based on the selected pref
 *FIXME set blog text based on the selected pref
 */
public class PreferencesActivity extends PreferenceActivity implements Codes, OnPreferenceChangeListener
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);

		//sert summaries
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		final Preference frequency = findPreference(PREF_FREQUENCY);
		frequency.setSummary(getTitleForValue(R.array.freqTitles, R.array.freqValues, preferences.getString(
				PREF_FREQUENCY, DEFAULT_FREQUENCY)));
		frequency.setOnPreferenceChangeListener(this);

		final Preference feed = findPreference(PREF_FEED);
		feed.setSummary(getTitleForValue(R.array.feedTitles, R.array.feedValues, preferences.getString(PREF_FEED,
				DEFAULT_FEED)));
		feed.setOnPreferenceChangeListener(this);

	}

	private CharSequence getTitleForValue( final int titles, final int values, final String value )
	{
		final String[] ta = getResources().getStringArray(titles);
		final String[] va = getResources().getStringArray(values);
		for ( int i = 0; i < va.length; i++ )
		{
			if ( va[i].equals(value) )
				return ta[i];
		}
		return null;
	}

	@Override
	public boolean onPreferenceChange( final Preference pref, final Object newValue )
	{
		if ( pref.getKey().equals(PREF_FEED) )
		{
			pref.setSummary(getTitleForValue(R.array.feedTitles, R.array.feedValues, (String) newValue));
			//FIXME delete database content and start synch in background
			try
			{
				RSSStream.deleteItems(getBaseContext());
				RSSStream.readAndStoreContent(getBaseContext(), new URL((String) newValue));
			}
			catch ( final Exception e )
			{
				Log.e(TAG, "Failed to update the stream.", e);
				final Toast toast = Toast.makeText(getBaseContext(), "Nem sikerült frissíteni a hírcsatornát.",
						Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
		}
		else if ( pref.getKey().equals(PREF_FREQUENCY) )
		{
			pref.setSummary(getTitleForValue(R.array.freqTitles, R.array.freqValues, (String) newValue));
			//FIXME update service in background if necessarys
		}
		return true;
	}
}
