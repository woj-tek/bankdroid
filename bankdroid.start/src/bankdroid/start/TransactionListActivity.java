package bankdroid.start;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Amount;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.AccountHistoryService;
import com.csaba.connector.service.AccountService;
import com.csaba.util.Formatters;

/**
 * @author Gabe
 */
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
				Amount credit = adapter.getCredits();
				Amount debit = adapter.getDebits();

				if ( credit == null && debit == null )
				{
					//no transaction
					( (TextView) findViewById(R.id.totals) ).setText(getString(R.string.warnNoTransaction));
				}
				else
				{
					if ( credit == null )
					{
						credit = new Amount(0, debit.getCurrency());
					}
					else if ( debit == null )
					{
						debit = new Amount(0, credit.getCurrency());
					}

					final Amount total = credit.add(debit);

					final Format format = Formatters.getCurrencyFormat(credit.getCurrency());
					final String creditString = format.format(credit.getAmount());
					final String debitString = format.format(Math.abs(debit.getAmount()));

					final String totalText = MessageFormat.format(getString(R.string.totals), creditString,
							debitString, total, GUIUtil.getHtmlColor(credit.getAmount()), GUIUtil.getHtmlColor(debit
									.getAmount()), GUIUtil.getHtmlColor(total.getAmount()));

					( (TextView) findViewById(R.id.totals) ).setText(Html.fromHtml(totalText));
				}
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
