package hu.tminfo;

import java.util.HashSet;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Gabe
 */
public class PreferencesActivity extends PreferenceActivity implements Codes, OnPreferenceChangeListener
{

	private String[] feedTitles;

	private final Set<Integer> feedRemoved = new HashSet<Integer>();
	private final Set<Integer> feedAdded = new HashSet<Integer>();

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

		final Preference expiry = findPreference(PREF_EXPIRY);
		expiry.setSummary(getTitleForValue(R.array.expTitles, R.array.expValues, preferences.getString(PREF_EXPIRY,
				DEFAULT_EXPIRY)));
		expiry.setOnPreferenceChangeListener(this);

		//build feed list dynamically
		final PreferenceGroup feedGroup = (PreferenceGroup) findPreference(PREF_FEEDGROUP);

		feedTitles = getResources().getStringArray(R.array.feedTitles);
		final String[] feedSummary = getResources().getStringArray(R.array.feedSummary);

		final int l = feedTitles.length;
		for ( int i = 0; i < l; i++ )
		{
			final Preference feedCheck = new CheckBoxPreference(getBaseContext());
			feedCheck.setKey(PREF_FEED_PREFIX + i);
			feedCheck.setDefaultValue(false);
			feedCheck.setTitle(feedTitles[i]);
			feedCheck.setSummary(feedSummary[i]);
			feedCheck.setPersistent(true);
			feedCheck.setOnPreferenceChangeListener(this);

			feedGroup.addPreference(feedCheck);
		}
	}

	public CharSequence getTitleForValue( final int titles, final int values, final String value )
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
		if ( pref.getKey().equals(PREF_FREQUENCY) )
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
		else if ( pref.getKey().equals(PREF_EXPIRY) )
		{
			pref.setSummary(getTitleForValue(R.array.expTitles, R.array.expValues, (String) newValue));
		}
		else if ( pref.getKey().startsWith(PREF_FEED_PREFIX) )
		{
			final boolean value = (Boolean) newValue;
			if ( !value )
			{
				//check if at least one feed was selected
				final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				boolean isAnySelected = false;
				final int l = feedTitles.length;
				for ( int i = 0; i < l; i++ )
				{
					if ( !pref.getKey().equals(PREF_FEED_PREFIX + i) )
						isAnySelected = preferences.getBoolean(PREF_FEED_PREFIX + i, false);

					if ( isAnySelected )
						break;
				}

				if ( !isAnySelected )
				{
					final Toast toast = Toast.makeText(getBaseContext(), "Legalább egy feedet ki kell választani.",
							Toast.LENGTH_LONG);
					toast.show();
					return false;
				}
			}

			//update list of modified feeds
			final int index = Integer.parseInt(pref.getKey().substring(PREF_FEED_PREFIX.length()));
			if ( value )
			{
				if ( feedRemoved.contains(index) )
					feedRemoved.remove(index);
				else
					feedAdded.add(index);
			}
			else
			{
				if ( feedAdded.contains(index) )
					feedAdded.remove(index);
				else
					feedRemoved.add(index);
			}
		}
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		feedAdded.clear();
		feedRemoved.clear();
		Log.d(TAG, "Clearing buffer.");
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		if ( !feedAdded.isEmpty() || !feedRemoved.isEmpty() )
		{
			final Intent intent = new Intent(ACTION_FEED_CHANGED);
			intent.setClass(getBaseContext(), RSSSyncService.class);

			if ( !feedAdded.isEmpty() )
			{
				final Integer[] IA = feedAdded.toArray(new Integer[feedAdded.size()]);
				final int[] added = new int[IA.length];
				for ( int i = 0; i < added.length; i++ )
				{
					added[i] = IA[i];
				}

				intent.putExtra(EXTRA_FEEDS_ADDED, added);
			}
			if ( !feedRemoved.isEmpty() )
			{
				final Integer[] IA = feedRemoved.toArray(new Integer[feedRemoved.size()]);
				final int[] removed = new int[IA.length];
				for ( int i = 0; i < removed.length; i++ )
				{
					removed[i] = IA[i];
				}
				intent.putExtra(EXTRA_FEEDS_REMOVED, removed);
			}
			startService(intent);
		}
	}
}
