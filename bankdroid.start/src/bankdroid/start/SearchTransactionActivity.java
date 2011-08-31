package bankdroid.start;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Account;
import com.csaba.connector.service.AccountService;
import com.csaba.util.Formatters;

/**
 * @author Gabe
 */
public class SearchTransactionActivity extends ServiceActivity implements OnClickListener
{
	private static int DIALOG_ACCOUNT_SELECT = 91345;
	private static int DIALOG_DATE_PICKER = 91346;
	private static int DIALOG_PERIOD_PICKER = 91347;
	private static int DIALOG_ACCOUNT_ERROR = 91348;

	private Account[] accounts;
	private TransactionFilter filter;
	private int dateView;
	private final Calendar dateValue = Calendar.getInstance();

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.searchtransaction);
		GUIUtil.setTitle(this, R.string.searchTranTitle);

		//add click listeners
		findViewById(R.id.accountSelect).setOnClickListener(this);
		findViewById(R.id.periodTemplate).setOnClickListener(this);
		findViewById(R.id.dateFrom).setOnClickListener(this);
		findViewById(R.id.dateTo).setOnClickListener(this);
		findViewById(R.id.search).setOnClickListener(this);

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

			datePicker.updateDate(dateValue.get(Calendar.YEAR), dateValue.get(Calendar.MONTH),
					dateValue.get(Calendar.DAY_OF_MONTH));
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
			builder.setTitle(getString(R.string.accountPicker));
			builder.setItems(accountNames, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick( final DialogInterface dialog, final int item )
				{
					filter.setAccount(item == 0 ? null : accounts[item - 1]);
					updateAccountSelect();
				}

			});
			return builder.create();
		}
		else if ( id == DIALOG_PERIOD_PICKER )
		{
			final String[] periods = getResources().getStringArray(R.array.periodTemplates);

			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.periodPicker));
			builder.setItems(periods, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick( final DialogInterface dialog, final int item )
				{
					updateDatePeriod(item);
				}

			});
			return builder.create();
		}
		else if ( id == DIALOG_ACCOUNT_ERROR )
		{

			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.errorTitle));
			builder.setMessage(R.string.msgAccountError);
			builder.setNeutralButton(R.string.close, new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick( final DialogInterface dialog, final int which )
				{
					dialog.dismiss();
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

	private void updateDatePeriod( final int periodIndex )
	{
		final Calendar cal = Calendar.getInstance();
		final Date to = cal.getTime();
		switch ( periodIndex )
		{
		case 0: //LAST WEEK
			cal.add(Calendar.DATE, -7);
			break;

		case 1: //LAST 30 DAYS
			cal.add(Calendar.DATE, -30);
			break;

		case 2: //FIRST OF THE MONTH
			cal.set(Calendar.DAY_OF_MONTH, 1);
			break;

		case 3: //LAST DATE OF LAST MONTH
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.DATE, -1);
			break;

		case 4: //FIRST DAY OF LAST MONTH
			cal.add(Calendar.MONTH, -1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			break;

		default:
			Log.e(TAG, "Unknown period index selected: " + periodIndex);
			break;
		}
		final Date from = cal.getTime();

		updateDate(R.id.dateFrom, from);
		updateDate(R.id.dateTo, to);
		filter.setFrom(from);
		filter.setTo(to);

		( (Button) findViewById(R.id.periodTemplate) )
				.setText(getResources().getStringArray(R.array.periodTemplates)[periodIndex]);
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
			if ( accounts == null )
			{
				showDialog(DIALOG_ACCOUNT_ERROR);
			}
			else
			{
				showDialog(DIALOG_ACCOUNT_SELECT);
			}
		}
		else if ( v.getId() == R.id.periodTemplate )
		{
			showDialog(DIALOG_PERIOD_PICKER);
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
			startActivityForResult(tranList, REQUEST_OTHER);
		}
	}

}
