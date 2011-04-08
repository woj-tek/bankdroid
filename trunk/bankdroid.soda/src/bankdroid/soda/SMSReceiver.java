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
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.text.ClipboardManager;
import android.util.Log;
import bankdroid.soda.bank.Bank;

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

				final Bank[] source = BankManager.findByPhoneNumber(context, sms.getOriginatingAddress().trim());

				if ( source != null && source.length > 0 )
				{
					//XXX known bug: this method does not return the full SMS if it is longer than 156 character.
					String message = sms.getMessageBody();
					message = message.replace('\n', ' ');
					message = message.replace('\r', ' ');

					boolean found = false;
					for ( final Bank bank : source )
					{
						final String code = bank.extractCode(message);

						if ( code != null )
						{
							found = true;
							processCode(context, bank, message, code);

							break;
						}
					}

					if ( !found )
					{
						Log.d(TAG, "Not an OTP message: '" + message + "'");
					}
				}
				else
				{
					Log.d(TAG, "Unrecognized phone number: " + sms.getOriginatingAddress());
				}
			}
		}
	}

	private void processCode( final Context context, final Bank source, final String message, final String code )
	{
		// Restore preferences
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		final boolean notificationOnly = settings.getBoolean(PREF_NOTIFICATION, DEFAULT_NOTIFICATION);
		final boolean keepSMS = settings.getBoolean(PREF_KEEP_SMS, DEFAULT_KEEP_SMS);
		final boolean autoCopy = settings.getBoolean(PREF_AUTO_COPY, DEFAULT_AUTO_COPY);
		final int codeCount = settings.getInt(PREF_CODE_COUNT, 0) + 1;
		final Editor edit = settings.edit();
		edit.putInt(PREF_CODE_COUNT, codeCount);
		edit.commit();

		if ( autoCopy )
		{
			( (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE) ).setText(code);
		}

		if ( notificationOnly )
		{
			//display notification
			final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			//create notification
			final int icon = android.R.drawable.stat_sys_warning;
			final CharSequence tickerText = MessageFormat.format(context.getText(R.string.notificationTicker)
					.toString(), source.getName());
			final long when = System.currentTimeMillis();

			final Notification notification = new Notification(icon, tickerText, when);

			//set extended message
			final CharSequence contentTitle = context.getText(R.string.notificationTitle);
			final CharSequence contentText = MessageFormat.format(
					context.getText(R.string.notificationText).toString(), source.getName());

			final Intent notificationIntent = new Intent(context, bankdroid.soda.SMSOTPDisplay.class);
			notificationIntent.setAction(ACTION_DISPLAY);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			notificationIntent.putExtra(BANKDROID_SODA_SMSMESSAGE, message);
			notificationIntent.putExtra(BANKDROID_SODA_BANK, source);
			notificationIntent.putExtra(BANKDROID_SODA_SMSCODE, code);
			notificationIntent.putExtra(BANKDROID_SODA_SMSTIMESTAMP, Calendar.getInstance().getTime());

			final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

			//display notification
			nm.notify(NOTIFICATION_ID, notification);
		}
		else
		{
			//start display activity directly.
			final Intent myIntent = new Intent();
			myIntent.setAction(ACTION_DISPLAY);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			myIntent.setClassName("bankdroid.soda", "bankdroid.soda.SMSOTPDisplay");
			myIntent.putExtra(BANKDROID_SODA_SMSMESSAGE, message);
			myIntent.putExtra(BANKDROID_SODA_BANK, source);
			myIntent.putExtra(BANKDROID_SODA_SMSCODE, code);
			myIntent.putExtra(BANKDROID_SODA_SMSTIMESTAMP, Calendar.getInstance().getTime());
			context.startActivity(myIntent);
		}

		if ( !keepSMS )
		{
			Log.d(TAG, "SMS should not be persisted. Aborting broadcast...");
			abortBroadcast();
		}
	}

}
