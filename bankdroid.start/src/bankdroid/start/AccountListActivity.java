package bankdroid.start;

import java.text.MessageFormat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Account;
import com.csaba.connector.service.AccountService;

public class AccountListActivity extends ServiceActivity implements OnItemClickListener
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.accountlist);
		GUIUtil.setTitle(this, R.string.accountListTitle);

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
		final long id = ( (AdapterContextMenuInfo) item.getMenuInfo() ).id;
		final AccountAdapter adapter = (AccountAdapter) ( (ListView) findViewById(R.id.accountList) ).getAdapter();
		final Account account = (Account) adapter.getItemById(id);

		if ( item.getItemId() == R.id.quickHistory )
		{
			quickHistory(account);
		}
		if ( item.getItemId() == R.id.shareDetails )
		{
			shareDetails(account);
		}
		if ( item.getItemId() == R.id.viewDetails )
		{
			viewDetails(account);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
	{
		final AccountAdapter adapter = (AccountAdapter) parent.getAdapter();

		final Account account = (Account) adapter.getItem(position);

		viewDetails(account);
	}

	private void shareDetails( final Account account )
	{
		PropertyViewActivity.shareDetails(this, "shareAccountDetails", //
				MessageFormat.format(getString(R.string.shareAccountSubject), GUIUtil.getAccountName(account)), //
				getString(R.string.shareAccountBodyTop),//
				PropertyHelper.getProperties(this, account));
	}

	private void quickHistory( final Account account )
	{
		trackClickEvent(ACTION_CLICK, "quickHistory");

		final Intent tranList = new Intent(getApplicationContext(), TransactionListActivity.class);
		final TransactionFilter filter = TransactionFilter.getDefaultFilter();
		filter.setAccount(account);
		tranList.putExtra(EXTRA_TRANSACTION_FILTER, filter);
		startActivityForResult(tranList, REQUEST_OTHER);
	}

	private void viewDetails( final Account account )
	{
		trackClickEvent(ACTION_CLICK, "viewAccountDetails");

		final Intent intent = new Intent(getBaseContext(), PropertyViewActivity.class);
		intent.putExtra(EXTRA_PROPERTIES, PropertyHelper.getProperties(this, account));
		intent.putExtra(EXTRA_ACTIVITY_TITLE, getString(R.string.accountDetailTitle));
		intent.putExtra(EXTRA_ANALYTICS_ACTION, "shareAccountDetails");
		intent.putExtra(EXTRA_SHARE_SUBJECT,
				MessageFormat.format(getString(R.string.shareAccountSubject), GUIUtil.getAccountName(account)));
		intent.putExtra(EXTRA_SHARE_BODY_TOP, getString(R.string.shareAccountBodyTop));

		startActivityForResult(intent, REQUEST_OTHER);
	}
}
