package bankdroid.start;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bankdroid.start.plugin.PluginManager;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.AccountService;

public class AccountListActivity extends ServiceActivity
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.accountlist);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		final Session session = SessionManager.getInstance().getSession();
		if ( session == null )
			return;

		( (TextView) findViewById(R.id.customerName) ).setText(session.getCustomer().getName());
		( (ImageView) findViewById(R.id.bankLogo) ).setImageDrawable(PluginManager.getIconDrawable(session.getBank()
				.getLargeIcon()));

		SessionManager.getInstance().getAccounts(this);
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		final AccountService accountService = (AccountService) service;

		final AccountAdapter adapter = new AccountAdapter(accountService.getAccounts());
		( (ListView) findViewById(R.id.accountList) ).setAdapter(adapter);
	}

}
