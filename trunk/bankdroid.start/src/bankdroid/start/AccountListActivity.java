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
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Account;
import com.csaba.connector.service.AccountService;

public class AccountListActivity extends ServiceActivity
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.accountlist);

		registerForContextMenu(findViewById(R.id.accountList));
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Log.d(TAG, "onResume()");

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
		final AccountService accountService = (AccountService) service;

		final AccountAdapter adapter = new AccountAdapter(accountService.getAccounts());
		( (ListView) findViewById(R.id.accountList) ).setAdapter(adapter);
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

}
