package bankdroid.start;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import bankdroid.util.Formatters;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Amount;
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
				: GUIUtil.getAccountName(filter.getAccount()), shortFormat.format(filter.getFrom()), shortFormat
				.format(filter.getTo()));
		( (TextView) findViewById(R.id.filterOptions) ).setText(summary);

		( (TextView) findViewById(R.id.totals) ).setText("");
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

			try
			{
				final Amount credit = adapter.getCredits();
				final Amount debits = adapter.getDebits();
				final Amount total = credit.add(debits);

				( (TextView) findViewById(R.id.totals) ).setText(MessageFormat.format(getString(R.string.totals),
						credit, debits, total));
			}
			catch ( final Exception e )
			{
				Log.d(TAG, "Currency problem: " + e.toString());
				( (TextView) findViewById(R.id.totals) ).setText(getString(R.string.variousCurrencies));
			}

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
