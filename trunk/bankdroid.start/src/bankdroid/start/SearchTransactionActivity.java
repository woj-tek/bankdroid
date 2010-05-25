package bankdroid.start;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Account;
import com.csaba.connector.service.AccountService;
import com.csaba.util.Formatters;

public class SearchTransactionActivity extends ServiceActivity implements OnClickListener
{
	private static int DIALOG_ACCOUNT_SELECT = 91345;
	private static int DIALOG_DATE_PICKER = 91346;

	private Account[] accounts;
	private TransactionFilter filter;
	private int dateView;
	private final Calendar dateValue = Calendar.getInstance();

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.searchtransaction);

		//add click listeners
		findViewById(R.id.accountSelect).setOnClickListener(this);
		findViewById(R.id.dateFrom).setOnClickListener(this);
		findViewById(R.id.dateTo).setOnClickListener(this);
		findViewById(R.id.search).setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if ( !SessionManager.getInstance().isLoggedIn() )
			return;

		filter = TransactionFilter.getDefaultFilter();
		updateAccountSelect();

		updateDate(R.id.dateFrom, filter.getFrom());
		updateDate(R.id.dateTo, filter.getTo());

		SessionManager.getInstance().getAccounts(this);
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);
		if ( service instanceof AccountService )
		{
			accounts = ( (AccountService) service ).getAccounts();
		}
	}

	@Override
	protected void onPrepareDialog( final int id, final Dialog dialog )
	{
		if ( id == DIALOG_DATE_PICKER )
		{

			final DatePickerDialog datePicker = (DatePickerDialog) dialog;

			datePicker.updateDate(dateValue.get(Calendar.YEAR), dateValue.get(Calendar.MONTH), dateValue
					.get(Calendar.DAY_OF_MONTH));
		}

		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog( final int id )
	{
		if ( id == DIALOG_ACCOUNT_SELECT )
		{
			final String[] accountNames = new String[accounts.length + 1];
			accountNames[0] = getString(R.string.all);
			for ( int i = 1; i < accountNames.length; i++ )
			{
				accountNames[i] = GUIUtil.getAccountName(accounts[i - 1]);
			}

			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.datePicker));
			builder.setItems(accountNames, new DialogInterface.OnClickListener()
			{
				public void onClick( final DialogInterface dialog, final int item )
				{
					filter.setAccount(item == 0 ? null : accounts[item - 1]);
					updateAccountSelect();
				}

			});
			return builder.create();
		}
		else if ( id == DIALOG_DATE_PICKER )
		{
			final DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
			{

				@Override
				public void onDateSet( final DatePicker view, final int year, final int monthOfYear,
						final int dayOfMonth )
				{
					dateValue.set(Calendar.YEAR, year);
					dateValue.set(Calendar.MONTH, monthOfYear);
					dateValue.set(Calendar.DAY_OF_MONTH, dayOfMonth);

					final Date time = dateValue.getTime();
					if ( dateView == R.id.dateFrom )
					{
						filter.setFrom(time);
					}
					else
					{
						filter.setTo(time);
					}

					updateDate(dateView, time);
				}
			}, dateValue.get(Calendar.YEAR), dateValue.get(Calendar.MONTH), dateValue.get(Calendar.DAY_OF_MONTH));
			return dialog;
		}
		return super.onCreateDialog(id);
	}

	private void updateDate( final int viewId, final Date value )
	{
		final Button button = ( (Button) findViewById(viewId) );
		button.setText(Formatters.getShortDateFormat().format(value));
	}

	protected void updateAccountSelect()
	{
		final Button button = ( (Button) findViewById(R.id.accountSelect) );
		button.setText(filter.getAccount() == null ? getString(R.string.all) : GUIUtil.getAccountName(filter
				.getAccount()));
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.accountSelect )
		{
			showDialog(DIALOG_ACCOUNT_SELECT);
		}
		else if ( v.getId() == R.id.dateFrom )
		{
			dateView = R.id.dateFrom;
			dateValue.setTime(filter.getFrom());
			showDialog(DIALOG_DATE_PICKER);
		}
		else if ( v.getId() == R.id.dateTo )
		{
			dateView = R.id.dateTo;
			dateValue.setTime(filter.getTo());
			showDialog(DIALOG_DATE_PICKER);
		}
		else if ( v.getId() == R.id.search )
		{
			final Intent tranList = new Intent(getApplicationContext(), TransactionListActivity.class);
			tranList.putExtra(EXTRA_TRANSACTION_FILTER, filter);
			startActivity(tranList);
		}
	}

}
