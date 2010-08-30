package bankdroid.start;

import android.view.View;

public class LogoutActivity extends AboutActivity
{
	@Override
	protected void initEventHandlers()
	{
		findViewById(R.id.closeButton).setOnClickListener(this);
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
		else
		{
			super.onClick(v);
		}
	}
}
