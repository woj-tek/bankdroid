package bankdroid.start.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import bankdroid.start.Eula;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.plugin.PluginManager;

public class AuthStartActivity extends ServiceActivity implements OnClickListener
{
	private final static int REQUEST_NEWUSER = 1001;

	private boolean onFirstDisplay = true;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		Eula.show(this);

		setSessionOriented(false);
		setShowHomeMenu(false);

		PluginManager.init();

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.authstart);

		findViewById(R.id.newUser).setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Log.d(TAG, "AuthStart resume");

		//FIXME fill user list here

		if ( onFirstDisplay && true ) //FIXME has no user set yet
		{
			onFirstDisplay = false;
			createNewUser();
		}
	}

	private void createNewUser()
	{
		//FIXME handle this correctly
		startActivityForResult(new Intent(this, AuthBankSelectActivity.class), REQUEST_NEWUSER);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.newUser )
		{
			createNewUser();
		}
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( requestCode == REQUEST_NEWUSER && resultCode == RESULT_OK )
		{
			Log.d(TAG, "AuthStart - finish it well.");
			setResult(RESULT_OK);
			finish();
		}
	}
}
