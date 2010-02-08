package bankdroid.andralytics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public class MainActivity extends TrackedActivity implements OnClickListener
{
	public static final String SHORTED_SITE_URL = "http://bit.ly/cOOLc0";
	public static final String SHORTED_MARKET_URL = "http://bit.ly/aQjh1K";

	/** 
	 * Called when the activity is first created.
	 * FIXME implement I18N
	 * FIXME add Hungarian translation
	 * 
	 * http://market.android.com/details?id=bankdroid.andralytics
	 * - http://bit.ly/aQjh1K
	 **/
	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);

		findViewById(R.id.header).setOnClickListener(this);
		findViewById(R.id.licenseLink).setOnClickListener(this);

		final String shortText = "Join Android community statistics...";
		final String twitterText = "Join Android community statistics...Download app from Market:";

		ShareUtils.shareInMail(findViewById(R.id.gmailButton), SHORTED_SITE_URL, shortText); // FIXME make more meeningful message body.
		ShareUtils.shareOnFacebook(findViewById(R.id.facebookButton), SHORTED_SITE_URL, shortText);
		ShareUtils.shareOnMySpace(findViewById(R.id.myspaceButton), SHORTED_SITE_URL, shortText);
		ShareUtils.shareOnTwitter(findViewById(R.id.twitterButton), SHORTED_MARKET_URL, twitterText);

		tracker.setDispatchPeriod(60);
	}

	@Override
	public void onClick( final View view )
	{
		if ( view.getId() == R.id.header )
		{
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SHORTED_SITE_URL)));
		}
		if ( view.getId() == R.id.licenseLink )
		{
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.licenseLink))));
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		( new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Thread.sleep(1000);

					tracker.dispatch();
				}
				catch ( final Exception e )
				{
					// skip exception
				}
			}
		}) ).start();
	}
}