package bankdroid.soda;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * @author gyenes
 *
 */
public class BankListActivity extends Activity implements Codes, OnItemClickListener
{
	SimpleCursorAdapter adapter;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.banklist);

		final Cursor cursor = getContentResolver().query(Bank.CONTENT_URI,
				new String[] { Bank.F__ID, Bank.F_NAME, Bank.F_PHONENUMBERS, Bank.F_COUNTRY }, null, null,
				Bank.DEFAULT_SORT_ORDER);

		startManagingCursor(cursor);

		final String[] columns = new String[] { Bank.F_NAME, Bank.F_PHONENUMBERS };
		final int[] names = new int[] { R.id.bankName, R.id.phoneNumber };

		adapter = new SimpleCursorAdapter(this, R.layout.banklistitem, cursor, columns, names);
		final int nameIndex = cursor.getColumnIndex(Bank.F_NAME);
		final int countryIndex = cursor.getColumnIndex(Bank.F_COUNTRY);
		adapter.setViewBinder(new ViewBinder()
		{

			@Override
			public boolean setViewValue( final View view, final Cursor cursor, final int columnIndex )
			{
				if ( columnIndex == nameIndex )
				{
					( (TextView) view ).setText(new StringBuilder(cursor.getString(nameIndex)).append(" [").append(
							cursor.getString(countryIndex)).append(']'));
					return true;
				}
				return false;
			}
		});

		final ListView list = (ListView) findViewById(R.id.bankListView);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		registerForContextMenu(list);
	}

	@Override
	public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
	{
		if ( parent.getId() == R.id.bankListView )
		{
			Log.d(TAG, "Following pos is selected: " + position);
			startEdit(id);
		}
	}

	private void startEdit( final long id )
	{
		final Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setClass(getBaseContext(), BankEditActivity.class);
		intent.setData(Uri.withAppendedPath(Bank.CONTENT_URI, String.valueOf(id)));
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.addBank )
		{
			final Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setData(Bank.CONTENT_URI);
			intent.setClass(getBaseContext(), BankEditActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.deleteBank )
		{
			final long id = ( (AdapterContextMenuInfo) item.getMenuInfo() ).id;
			getContentResolver().delete(Uri.withAppendedPath(Bank.CONTENT_URI, String.valueOf(id)), null, null);
			final Toast succes = Toast.makeText(getBaseContext(), R.string.bankDeleted, Toast.LENGTH_SHORT);
			succes.show();
		}
		else if ( item.getItemId() == R.id.editBank )
		{
			startEdit(( (AdapterContextMenuInfo) item.getMenuInfo() ).id);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu( final ContextMenu menu, final View v, final ContextMenuInfo menuInfo )
	{
		if ( v.getId() == R.id.bankListView )
		{
			final MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.banklistcontextmenu, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onCreateOptionsMenu( final Menu menu )
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.banklistmenu, menu);
		return true;
	}
}
