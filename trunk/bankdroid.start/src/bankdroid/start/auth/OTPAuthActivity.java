package bankdroid.start.auth;

import android.os.Bundle;
import android.view.Window;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;

public class OTPAuthActivity extends ServiceActivity
{
	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.auth_otp);
	}

}
