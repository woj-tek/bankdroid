package hu.androidportal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

/**
 * @author Gabe
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

			final Intent intent = new Intent(ACTION_FEED_CHANGED);
			intent.setClass(getBaseContext(), RSSSyncService.class);
			intent.putExtra(PREF_FEED, (String) newValue);
			startService(intent);
		}
		else if ( pref.getKey().equals(PREF_FREQUENCY) )
		{
			pref.setSummary(getTitleForValue(R.array.freqTitles, R.array.freqValues, (String) newValue));

			if ( newValue.equals("0") )
			{
				RSSSyncService.clearSchedule(getBaseContext());
			}
			else
			{
				RSSSyncService.schedule(getBaseContext(), (String) newValue);
			}
		}
		return true;
	}
}
