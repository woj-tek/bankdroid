package bankdroid.start;

import android.content.Intent;
import android.view.View;

public class LogoutActivity extends AboutActivity
{
	@Override
	protected void initEventHandlers()
	{
		findViewById(R.id.closeButton).setOnClickListener(this);
		findViewById(R.id.loginButton).setOnClickListener(this);
	}

	@Override
	protected int getContentLayoutId()
	{
		return R.layout.logout;
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.closeButton )
		{
			finish();
		}
		else if ( v.getId() == R.id.loginButton )
		{
			startActivity(new Intent(this, MainActivity.class));//FIXME some parameter is need to start new instance of main activity
		}
		else
		{
			super.onClick(v);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		dispatch();
	}
}
