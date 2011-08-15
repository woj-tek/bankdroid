package bankdroid.soda;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author gyenes
 */
public class SMSListActivity extends MenuActivity implements OnItemClickListener
{
	private static final String SUBMISSION_ADDRESS = "sample@bankdroid.info";
	private final static int DIALOG_KNOWN_SMS = 678;

	private SimpleCursorAdapter adapter;

	private String[] addresses;
	private String[] bodies;
	private long[] timestamps;

	private int addressIndex;
	private int bodyIndex;
	private int timestampIndex;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.smslist);

		final Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"),
				new String[] { "_id", "address", "person", "body", "date" }, null, null, "date DESC");

		addressIndex = cursor.getColumnIndexOrThrow("address");
		bodyIndex = cursor.getColumnIndexOrThrow("body");
		timestampIndex = cursor.getColumnIndexOrThrow("date");

		startManagingCursor(cursor); //display person if known

		final String[] columns = new String[] { "address", "body" };
		final int[] names = new int[] { R.id.smsSender, R.id.smstext };

		adapter = new SimpleCursorAdapter(this, R.layout.smslistitem, cursor, columns, names);

		final ListView list = (ListView) findViewById(R.id.smsListView);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		final Cursor cursor = adapter.getCursor();

		//collect data from cursor
		cursor.moveToFirst();

		final int count = cursor.getCount();
		if ( count == 0 )
		{
			final Toast toast = Toast.makeText(getApplicationContext(), R.string.noSMSInInbox, Toast.LENGTH_SHORT);
			toast.show();

			Log.d(TAG, "There is no SMS in the inbox. Existing from SMS selection activity.");
			finish();
		}
		else
		{
			addresses = new String[count];
			bodies = new String[count];
			timestamps = new long[count];
			for ( int i = 0; i < count; i++ )
			{
				addresses[i] = cursor.getString(addressIndex);
				bodies[i] = cursor.getString(bodyIndex);
				timestamps[i] = cursor.getLong(timestampIndex);

				cursor.moveToNext();
			}

			cursor.moveToFirst();
		}
	}

	@Override
	public void onItemClick( final AdapterView<?> adapter, final View view, final int position, final long id )
	{
		final String address = addresses[position];
		final String body = bodies[position];
		final Date timestamp = new Date(timestamps[position]);

		Log.d(TAG, "SMS was selected: " + address + " :: " + body);

		//verify whether the SMS is already known or not
		final Message code = BankManager.getCode(this, address, body, timestamp, false);
		if ( code != null )
		{
			Log.w(TAG, "User selected known SMS as sample. Identified bank: " + code.getBank().getName());
			showDialog(DIALOG_KNOWN_SMS);
			return;
		}

		//XXX do some heuristic checks whether the message can be a password message or not. 

		//construct e-mail body
		final StringBuilder builder = new StringBuilder();

		builder.append(getString(R.string.emailPrefix)).append(" ");
		builder.append(getString(R.string.emailOriginator)).append(" ").append(address).append(". ");
		builder.append(getString(R.string.emailSMSText)).append(" ").append(body).append("\n");

		sendEmail(new String[] { SUBMISSION_ADDRESS }, getString(R.string.emailSubject), builder.toString());//no I18N
	}

	private void sendEmail( final String[] address, final String subject, final String msg )
	{
		final Intent view = new Intent(Intent.ACTION_VIEW);
		final StringBuilder uri = new StringBuilder("mailto:");
		uri.append(address[0]);
		uri.append("?subject=").append(Uri.encode(subject));
		uri.append("&body=").append(Uri.encode(msg));
		Log.d(TAG, "URI: " + uri);
		view.setData(Uri.parse(uri.toString()));
		startActivity(view);
	}

	@Override
	protected Dialog onCreateDialog( final int id )
	{
		final Dialog dialog;
		switch ( id )
		{
		case DIALOG_KNOWN_SMS:
			// do the work to define the pause Dialog
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.msgKnownSMS).setCancelable(false).setNeutralButton(R.string.ok,
					new DialogInterface.OnClickListener()
					{
						public void onClick( final DialogInterface dialog, final int id )
						{
							dialog.cancel();
						}
					});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

}
