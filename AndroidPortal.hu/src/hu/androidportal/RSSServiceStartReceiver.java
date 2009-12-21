package hu.androidportal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class RSSServiceStartReceiver extends BroadcastReceiver implements Codes
{

	@Override
	public void onReceive( final Context context, final Intent intent )
	{
		if ( Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) )
		{
			Log.d(TAG, "Boot completed.");
			RSSSyncService.schedule(context, null);
		}
		else if ( ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) )
		{
			final boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			Log.d(TAG, "Connectivity changed: " + noConnectivity);
			if ( noConnectivity )
			{
				RSSSyncService.clearSchedule(context);
			}
			else
			{
				RSSSyncService.schedule(context, null);
			}
		}
		else if ( ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED.equals(intent.getAction()) )
		{
			final boolean backgroundData = ( (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE) ).getBackgroundDataSetting();
			Log.d(TAG, "Background data service settings changed: " + backgroundData);
			if ( !backgroundData )
			{
				RSSSyncService.clearSchedule(context);
			}
			else
			{
				RSSSyncService.schedule(context, null);
			}
		}
		else if ( Codes.ACTION_SYNCH_NOW.equals(intent.getAction()) )
		{
			Log.d(TAG, "Alarm activated.");
			RSSSyncService.acquireLock(context);

			RSSSyncService.startService(context, ACTION_SYNCH_NOW);
		}
	}
}
