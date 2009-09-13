package bankdroid.soda;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

// +36309400700OTPdirekt - Belfoldi forint atutalas xxx szamlan yyy HUF osszeggel zzz szamlara. Azonosito: 90120437 
// +36303444504Az on kezdeti SpectraNet bejelentkezesi jelszava: 2HWNVRNJ
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
					final String code = source.getCode(message);

					if ( code != null )
					{
						// Restore preferences
						/*final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
						final boolean notification = settings.getBoolean(PREF_NOTIFICATION, false);
						final boolean keepSMS = settings.getBoolean(PREF_KEEP_SMS, false);*/

						//display the new SMS message
						final Intent myIntent = new Intent();
						myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						myIntent.setClassName("bankdroid.soda", "bankdroid.soda.SMSOTPDisplay");
						myIntent.putExtra(BANKDROID_SODA_SMSMESSAGE, message); // key/value pair, where key needs current package prefix.
						myIntent.putExtra(BANKDROID_SODA_BANK, source); // key/value pair, where key needs current package prefix.
						myIntent.putExtra(BANKDROID_SODA_SMSCODE, code); // key/value pair, where key needs current package prefix.
						myIntent.putExtra(BANKDROID_SODA_SMSTIMESTAMP, Calendar.getInstance().getTime()); // key/value pair, where key needs current package prefix.
						context.startActivity(myIntent);
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

/* Delete SMS from inbox
Uri uriSms = Uri.parse("content://sms/inbox");
Cursor c = getContentResolver().query(uriSms, null,null,null,null); 
int id = c.getInt(0);
int thread_id = c.getInt(1); //get the thread_id
getContentResolver().delete(Uri.parse("content://sms/conversations/" + thread_id),null,null);*/

/*
private long getThreadId(Context context) {
long threadId = 0;

String SMS_READ_COLUMN = "read";
String WHERE_CONDITION = SMS_READ_COLUMN + " = 0";
String SORT_ORDER = "date DESC";
int count = 0;

Cursor cursor = context.getContentResolver().query(
            SMS_INBOX_CONTENT_URI,
  new String[] { "_id", "thread_id", "address", "person", "date", "body" },
            WHERE_CONDITION,
            null,
            SORT_ORDER);

if (cursor != null) {
    try {
        count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            threadId = cursor.getLong(1);                              
        }
    } finally {
            cursor.close();
    }
}


return threadId;
}*/