package bankdroid.soda;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author gyenes
 *
 */
public class SMSListActivity extends Activity implements Codes, OnItemClickListener, OnClickListener
{
	private static final String SUBMISSION_ADDRESS = "sample@bankdroid.info";

	private SimpleCursorAdapter adapter;

	private String[] addresses;
	private String[] bodies;

	private int addressIndex;
	private int bodyIndex;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.smslist);

		final Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"),
				new String[] { "_id", "address", "person", "body" }, null, null, "date DESC");

		addressIndex = cursor.getColumnIndexOrThrow("address");
		bodyIndex = cursor.getColumnIndexOrThrow("body");

		startManagingCursor(cursor); //display person if known

		final String[] columns = new String[] { "address", "body" };
		final int[] names = new int[] { R.id.smsSender, R.id.smstext };

		adapter = new SimpleCursorAdapter(this, R.layout.smslistitem, cursor, columns, names);

		final ListView list = (ListView) findViewById(R.id.smsListView);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		( (Button) findViewById(R.id.help) ).setOnClickListener(this);
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
			for ( int i = 0; i < count; i++ )
			{
				addresses[i] = cursor.getString(addressIndex);
				bodies[i] = cursor.getString(bodyIndex);

				cursor.moveToNext();
			}

			cursor.moveToFirst();
		}
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		super.onActivityResult(requestCode, resultCode, data);

		if ( requestCode == REQUEST_EMAIL_SEND )
		{
			//TODO 2.0 find out whether the e-mail is sent or not.
			//setResult(RESULT_OK);
			//finish();
		}
	}

	@Override
	public void onItemClick( final AdapterView<?> adapter, final View view, final int position, final long id )
	{
		final String address = addresses[position];
		final String body = bodies[position];

		Log.d(TAG, "SMS was selected: " + address + " :: " + body);

		//TODO 2.0 forward action to bank name selection

		//construct e-mail body
		final StringBuilder builder = new StringBuilder();

		builder.append("Bank address: ").append(address).append("\n").append("\n"); //no I18N
		builder.append("SMS OTP text: ").append(body).append("\n");//no I18N

		sendEmail(new String[] { SUBMISSION_ADDRESS }, "SMS OTP Sample", builder.toString());//no I18N
	}

	private void sendEmail( final String[] address, final String subject, final String msg )
	{
		final Intent send = new Intent(Intent.ACTION_SEND);
		send.putExtra(Intent.EXTRA_EMAIL, address);
		send.putExtra(Intent.EXTRA_SUBJECT, subject);
		send.putExtra(Intent.EXTRA_TEXT, msg);
		//send.setType("message/rfc822");
		send.setType("text/plain");
		startActivityForResult(Intent.createChooser(send, getString(R.string.selectEmail)), REQUEST_EMAIL_SEND);
	}

	@Override
	public void onClick( final View arg0 )
	{
		if ( arg0.getId() == R.id.help )
		{
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_SUBMIT_SAMPLE));
			startActivity(viewIntent);
		}
	}

}
