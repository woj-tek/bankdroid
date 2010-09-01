package bankdroid.start;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Account;
import com.csaba.connector.service.AccountService;

public class AccountListActivity extends ServiceActivity implements OnItemClickListener
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.accountlist);

		final ListView list = (ListView) findViewById(R.id.accountList);
		registerForContextMenu(list);
		list.setOnItemClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if ( !SessionManager.getInstance().isLoggedIn() )
			return;

		SessionManager.getInstance().getAccounts(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		Log.d(TAG, "onPause()");

	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof AccountService )
		{
			final AccountService accountService = (AccountService) service;

			final AccountAdapter adapter = new AccountAdapter(accountService.getAccounts());
			( (ListView) findViewById(R.id.accountList) ).setAdapter(adapter);
		}
	}

	@Override
	public void onCreateContextMenu( final ContextMenu menu, final View v, final ContextMenuInfo menuInfo )
	{
		if ( v.getId() == R.id.accountList )
		{
			final MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.acclistcontextmenu, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.quickHistory )
		{
			final long id = ( (AdapterContextMenuInfo) item.getMenuInfo() ).id;
			final AccountAdapter adapter = (AccountAdapter) ( (ListView) findViewById(R.id.accountList) ).getAdapter();

			final Account account = (Account) adapter.getItemById(id);
			final Intent tranList = new Intent(getApplicationContext(), TransactionListActivity.class);
			final TransactionFilter filter = TransactionFilter.getDefaultFilter();
			filter.setAccount(account);
			tranList.putExtra(EXTRA_TRANSACTION_FILTER, filter);
			startActivity(tranList);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
	{
		final AccountAdapter adapter = (AccountAdapter) parent.getAdapter();

		final Account account = (Account) adapter.getItem(position);

		final Intent intent = new Intent(getBaseContext(), PropertyViewActivity.class);
		intent.putExtra(EXTRA_PROPERTIES, PropertyHelper.getProperties(this, account));
		intent.putExtra(EXTRA_ACTIVITY_TITLE, getString(R.string.accountDetailTitle));

		startActivity(intent);
	}
}
