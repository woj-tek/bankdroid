package bankdroid.soda;

import java.text.MessageFormat;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver implements Codes
{
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive( final Context context, final Intent intent )
	{
		if ( intent.getAction().equals(ACTION) )
		{
			final Bundle bundle = intent.getExtras();
			if ( bundle != null )
			{
				//retrieve the SMS message received
				final Object[] pdus = (Object[]) bundle.get("pdus");

				final SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);

				final Bank source = Bank.findByPhoneNumber(sms.getOriginatingAddress());

				if ( source != null )
				{
					//extract code
					final String message = sms.getMessageBody();
					final String code = source.extractCode(message);

					if ( code != null )
					{
						// Restore preferences
						final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
						final boolean notificationOnly = settings.getBoolean(PREF_NOTIFICATION, false);

						if ( notificationOnly )
						{
							//display notification
							final NotificationManager nm = (NotificationManager) context
									.getSystemService(Context.NOTIFICATION_SERVICE);

							//create notification
							final int icon = android.R.drawable.stat_sys_warning;
							final CharSequence tickerText = MessageFormat.format(context.getText(
									R.string.notificationTicker).toString(), source.getName());
							final long when = System.currentTimeMillis();

							final Notification notification = new Notification(icon, tickerText, when);

							//set extended message
							final CharSequence contentTitle = context.getText(R.string.notificationTitle);
							final CharSequence contentText = MessageFormat.format(context.getText(
									R.string.notificationText).toString(), source.getName());

							final Intent notificationIntent = new Intent(context, bankdroid.soda.SMSOTPDisplay.class);
							notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
							notificationIntent.putExtra(BANKDROID_SODA_SMSMESSAGE, message); // key/value pair, where key needs current package prefix.
							notificationIntent.putExtra(BANKDROID_SODA_BANK, source); // key/value pair, where key needs current package prefix.
							notificationIntent.putExtra(BANKDROID_SODA_SMSCODE, code); // key/value pair, where key needs current package prefix.
							notificationIntent.putExtra(BANKDROID_SODA_SMSTIMESTAMP, Calendar.getInstance().getTime()); // key/value pair, where key needs current package prefix.

							final PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
									notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

							notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

							//display notification
							nm.notify(NOTIFICATION_ID, notification);
						}
						else
						{
							//start display activity directly.
							final Intent myIntent = new Intent();
							myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
							myIntent.setClassName("bankdroid.soda", "bankdroid.soda.SMSOTPDisplay");
							myIntent.putExtra(BANKDROID_SODA_SMSMESSAGE, message); // key/value pair, where key needs current package prefix.
							myIntent.putExtra(BANKDROID_SODA_BANK, source); // key/value pair, where key needs current package prefix.
							myIntent.putExtra(BANKDROID_SODA_SMSCODE, code); // key/value pair, where key needs current package prefix.
							myIntent.putExtra(BANKDROID_SODA_SMSTIMESTAMP, Calendar.getInstance().getTime()); // key/value pair, where key needs current package prefix.
							context.startActivity(myIntent);
						}

					}
					else
					{
						Log.d(TAG, "Not an OTP message.");
					}
				}
				else
				{
					Log.d(TAG, "Unrecognized phone number: " + sms.getOriginatingAddress());
				}
			}
		}
	}
}
