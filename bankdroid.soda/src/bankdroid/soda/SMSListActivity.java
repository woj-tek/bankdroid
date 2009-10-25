package bankdroid.soda;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * FIXME make a better header for the screen. Include link to the user guide.
 * @author gyenes
 *
 */
public class SMSListActivity extends Activity implements Codes, OnItemClickListener
{
	private static final String SUBMISSION_ADDRESS = "sample@bankdroid.info";

	private static final int EMAIL_SEND = 1001;

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
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		final Cursor cursor = adapter.getCursor();

		//collect data from cursor
		cursor.moveToFirst();

		final int count = cursor.getCount();
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

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		super.onActivityResult(requestCode, resultCode, data);

		if ( requestCode == EMAIL_SEND )
		{
			//FIXME find out whether the e-mail is sent or not.
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

		//FIXME forward action to bank name selection

		//construct e-mail body
		final StringBuilder builder = new StringBuilder();

		builder.append("Bank address: ").append(address).append("\n").append("\n");
		builder.append("SMS OTP text: ").append(body).append("\n");

		sendEmail(new String[] { SUBMISSION_ADDRESS }, "SMS OTP Sample", builder.toString());
	}

	private void sendEmail( final String[] address, final String subject, final String msg )
	{
		final Intent send = new Intent(Intent.ACTION_SEND);
		send.putExtra(Intent.EXTRA_EMAIL, address);
		send.putExtra(Intent.EXTRA_SUBJECT, subject);
		send.putExtra(Intent.EXTRA_TEXT, msg);
		//send.setType("message/rfc822");
		send.setType("text/plain");
		startActivityForResult(Intent.createChooser(send, "Select account:"), EMAIL_SEND);
	}

}
