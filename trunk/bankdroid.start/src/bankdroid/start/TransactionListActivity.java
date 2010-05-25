package bankdroid.start;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Amount;
import com.csaba.connector.model.HistoryItem;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.AccountHistoryService;
import com.csaba.connector.service.AccountService;
import com.csaba.util.Formatters;

/**
 * @author Gabe
 */
public class TransactionListActivity extends ServiceActivity implements OnItemClickListener
{
	private TransactionAdapter adapter;
	private TransactionFilter filter;
	private Account[] accounts;
	private int lastItem;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.tranlist);

		adapter = new TransactionAdapter();
		final ListView list = (ListView) findViewById(R.id.transactionList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if ( !SessionManager.getInstance().isLoggedIn() )
			return;

		//process intent
		final Intent filterIntent = getIntent();
		if ( filterIntent != null )
		{
			setIntent(null); // clear intent to make sure that it is not called for new search

			filter = (TransactionFilter) filterIntent.getSerializableExtra(EXTRA_TRANSACTION_FILTER);

			//reset data fields
			final String template = getString(R.string.tranListSummary);

			final DateFormat shortFormat = Formatters.getShortDateFormat();
			final String summary = MessageFormat.format(template, filter.getAccount() == null ? getString(R.string.all)
					: GUIUtil.getAccountName(filter.getAccount()), shortFormat.format(filter.getFrom()), shortFormat
					.format(filter.getTo()));
			( (TextView) findViewById(R.id.filterOptions) ).setText(summary);

			( (TextView) findViewById(R.id.totals) ).setText("");

			//reset list
			adapter.setItems(new HistoryItem[0]);

			//start services	
			if ( filter.getAccount() == null )
				SessionManager.getInstance().getAccounts(this);
			else
				callHistoryService(filter.getAccount(), filter.getFrom(), filter.getTo());
		}
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
		super.onServiceFinished(service);
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

			if ( filter.getAccount() == null && accounts != null && lastItem < accounts.length - 1 )
			{
				lastItem++;

				callHistoryService(accounts[lastItem], filter.getFrom(), filter.getTo());
			}
		}
		else if ( service instanceof AccountService )
		{
			//account list received, get history of each account.
			accounts = ( (AccountService) service ).getAccounts();
			lastItem = 0;
			callHistoryService(accounts[lastItem], filter.getFrom(), filter.getTo());
		}
	}

	@Override
	public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
	{
		final TransactionAdapter adapter = (TransactionAdapter) parent.getAdapter();

		final HistoryItem item = (HistoryItem) adapter.getItem(position);

		final Intent intent = new Intent(getBaseContext(), PropertyViewActivity.class);

		final String[] defaultLabels = new String[5];
		final String[] defaultValues = new String[5];

		defaultLabels[0] = getString(R.string.accountNumber);
		defaultValues[0] = GUIUtil.getAccountName(item.getOwner());
		defaultLabels[1] = getString(R.string.transactionDate);
		defaultValues[1] = Formatters.getShortDateFormat().format(item.getDate());
		defaultLabels[2] = getString(R.string.transactionAmount);
		defaultValues[2] = item.getAmount().toString();
		defaultLabels[3] = getString(R.string.transactionDescription);
		defaultValues[3] = item.getDescription();
		defaultLabels[4] = getString(R.string.transactionBalance);
		defaultValues[4] = item.getBalance().toString();

		intent.putExtra(EXTRA_PROPERTY_DEFAULT_LABELS, defaultLabels);
		intent.putExtra(EXTRA_PROPERTY_DEFAULT_VALUES, defaultValues);

		intent.putExtra(EXTRA_PROPERTY_OBJECT, item);
		intent.putExtra(EXTRA_ACTIVITY_TITLE, getString(R.string.tranDetailTitle));
		startActivity(intent);
	}
}
