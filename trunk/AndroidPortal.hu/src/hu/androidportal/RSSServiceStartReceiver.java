package hu.androidportal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class RSSServiceStartReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive( final Context context, final Intent intent )
	{
		if ( Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) )
		{
			//FIXME start service on boot
		}
		else if ( ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) )
		{
			final boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			//FIXME handle connectity change event.
		}
		else if ( ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED.equals(intent.getAction()) )
		{
			//FIXME handle background data service changed event
		}
	}

}
