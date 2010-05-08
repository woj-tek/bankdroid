package bankdroid.start;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.AccountService;

public class AccountListActivity extends ServiceActivity
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.accountlist);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		try
		{
			final Session session = SessionManager.getInstance().getSession();
			final AccountService accounts = BankServiceFactory.getBankService(session.getBank(), AccountService.class);
			( new ServiceRunner(this, this, accounts, session) ).start();
		}
		catch ( final ServiceException e )
		{
			Log.e(TAG, "Failed to get list of Banks.", e);
			setDialogMessage("Getting account list failed.\n" + e.getMessage());//FIXME I18N
			showDialog(MESSAGE_DIALOG);
		}
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		final AccountService accountService = (AccountService) service;

		final AccountAdapter adapter = new AccountAdapter(accountService.getAccounts());
		( (ListView) findViewById(R.id.accountList) ).setAdapter(adapter);
	}

}
