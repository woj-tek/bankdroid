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

public class SMSListActivity extends Activity implements Codes, OnItemClickListener
{
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
	public void onItemClick( final AdapterView<?> adapter, final View view, final int position, final long id )
	{
		final String address = addresses[position];
		final String body = bodies[position];

		Log.d(TAG, " Selected item address and body: " + address + ":" + body);

		final Intent resultData = new Intent();
		resultData.putExtra(BANKDROID_SODA_ADDRESS, address);
		resultData.putExtra(BANKDROID_SODA_SMSMESSAGE, body);
		setResult(RESULT_OK, resultData);

		finish();
	}
}
