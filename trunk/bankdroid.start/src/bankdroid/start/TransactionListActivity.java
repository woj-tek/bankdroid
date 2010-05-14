package bankdroid.start;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bankdroid.start.plugin.PluginManager;
import bankdroid.util.Formatters;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.AccountHistoryService;
import com.csaba.connector.service.AccountService;

public class TransactionListActivity extends ServiceActivity
{
	private TransactionAdapter adapter;
	private TransactionFilter filter;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.tranlist);

		adapter = new TransactionAdapter();
		( (ListView) findViewById(R.id.transactionList) ).setAdapter(adapter);
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

		//process intent
		final Intent filterIntent = getIntent();
		filter = (TransactionFilter) filterIntent.getSerializableExtra(EXTRA_TRANSACTION_FILTER);
		if ( filter.getAccount() == null )
			SessionManager.getInstance().getAccounts(this);
		else
			callHistoryService(filter.getAccount(), filter.getFrom(), filter.getTo());

		//reset data fields
		final String template = getString(R.string.tranListSummary);

		final DateFormat shortFormat = Formatters.getShortDateFormat();
		final String summary = MessageFormat.format(template, filter.getAccount() == null ? getString(R.string.all)
				: filter.getAccount().getName(), shortFormat.format(filter.getFrom()), shortFormat.format(filter
				.getTo()));
		( (TextView) findViewById(R.id.filterOptions) ).setText(summary);

		( (TextView) findViewById(R.id.totalCredits) ).setText("");
		( (TextView) findViewById(R.id.totalDebits) ).setText("");
	}

	private void callHistoryService( final Account account, final Date from, final Date to )
	{
		try
		{
			final Session session = SessionManager.getInstance().getSession();
			final AccountHistoryService historyService = BankServiceFactory.getBankService(session.getBank(),
					AccountHistoryService.class);

			historyService.setAccount(account);
			historyService.setDateRange(from, to);

			( new ServiceRunner(this, this, historyService, session) ).start();
		}
		catch ( final ServiceException e )
		{
			onServiceFailed(null, e);
		}
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		if ( service instanceof AccountHistoryService )
		{
			final AccountHistoryService history = (AccountHistoryService) service;

			adapter.addItems(history.getHistory());

			String credits = "";
			String debits = "";

			try
			{
				credits = adapter.getCredits().toString();
				debits = adapter.getDebits().toString();
			}
			catch ( final Exception e )
			{
				Log.d(TAG, "Currency problem: " + e.toString());
				credits = debits = getString(R.string.variousCurrencies);
			}

			( (TextView) findViewById(R.id.totalCredits) ).setText(MessageFormat.format(
					getString(R.string.totalCredit), credits));
			( (TextView) findViewById(R.id.totalDebits) ).setText(MessageFormat.format(getString(R.string.totalDebit),
					debits));

		}
		else if ( service instanceof AccountService )
		{
			//account list received, get history of each account.
			final Account[] accounts = ( (AccountService) service ).getAccounts();
			for ( final Account account : accounts )
			{
				callHistoryService(account, filter.getFrom(), filter.getTo());
			}
		}
	}
}
