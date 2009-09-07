package bankdroid.soda;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;

public class SMSReceiver extends BroadcastReceiver
{
	public static final String BANKDROID_SOD_SMSMESSAGE = "bankdroid.sod.SMSMessage";

	public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	public static final String BANKDROID_SOD_SMSTIMESTAMP = "bankdroid.sod.SMSTimestamp";

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
				final SmsMessage[] msgs = new SmsMessage[pdus.length];
				for ( int i = 0; i < msgs.length; i++ )
				{
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}

				final String messageINeed = msgs[0].getMessageBody();

				//display the new SMS message
				final Intent myIntent = new Intent();
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				myIntent.setClassName("bankdroid.sod", "bankdroid.sod.SMSOTPDisplay");
				myIntent.putExtra(BANKDROID_SOD_SMSMESSAGE, messageINeed); // key/value pair, where key needs current package prefix.
				myIntent.putExtra(BANKDROID_SOD_SMSTIMESTAMP, Calendar.getInstance().getTime()); // key/value pair, where key needs current package prefix.
				context.startActivity(myIntent);
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