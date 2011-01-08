package hu.tminfo;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;

public class ToolbarActivity extends TrackedActivity implements Codes
{

	protected boolean useToolbar = DEFAULT_USE_TOOLBAR;

	private final String prefToolbarState;

	public ToolbarActivity()
	{

		super();

		prefToolbarState = Codes.PREF_TOOLBAR_STATE + getClass().getName();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		//initialize toolbar
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		useToolbar = preferences.getBoolean(PREF_USE_TOOLBAR, DEFAULT_USE_TOOLBAR);
		boolean showToolbar = false;
		if ( useToolbar )
		{
			showToolbar = preferences.getBoolean(prefToolbarState, true);
		}
		findViewById(R.id.toolbar).setVisibility(showToolbar ? View.VISIBLE : View.INVISIBLE);

	}

	@Override
	public boolean onKeyUp( final int keyCode, final KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_MENU && useToolbar )
		{
			final View toolbar = findViewById(R.id.toolbar);
			final boolean showToolbar = toolbar.isShown();
			if ( showToolbar )
				toolbar.setVisibility(View.INVISIBLE);
			else
				toolbar.setVisibility(View.VISIBLE);
			final SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			final Editor edit = preferences.edit();
			edit.putBoolean(prefToolbarState, !showToolbar);
			edit.commit();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

}