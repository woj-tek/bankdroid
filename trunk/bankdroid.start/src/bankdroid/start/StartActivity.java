package bankdroid.start;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import bankdroid.start.plugin.PluginManager;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;
import com.csaba.connector.service.LoginService;

/**
 * @author Gabe
 *
 *  TODO take care of session timeout 
 *  TODO add hungarian translation
 *
 *  XXX use AccountManager to handle stored passowrds
 *  XXX shortcut widget for the various services - direct access to the service. These widgets should go through the mainactivity.
 *  XXX share account information link for account numbers, account details, transaction details, etc..
 *  XXX toolbars instead of menu
 *  XXX analytics on the clicks. 
 *
 *	XXX make plugins for various login screens
 *	XXX enable different flow for login (to support SMS OTP)
 *  XXX add OTP plugin
 *  XXX add Citibank plugin
 *  XXX add K&H plugin
 */
public class StartActivity extends ServiceActivity implements OnClickListener
{
	private static final int BANK_SELECT_DIALOG = 999122;
	private Bank[] banks;
	private String loginId = DEFAULT_LOGINID;
	private String password = DEFAULT_PASSWORD;
	protected Bank bankSelected;

	private boolean showDummyBank = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		PluginManager.init();
		try
		{
			banks = BankServiceFactory.getAvailableBanks();
			for ( final Bank bank : banks )
			{
				Log.d(TAG, "Bank found: " + bank.getId());
			}
		}
		catch ( final Exception e )
		{
			Log.e(TAG, "Failed to initialize bank list.", e);
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.start);

		( (Button) findViewById(R.id.loginButton) ).setOnClickListener(this);
		( (Button) findViewById(R.id.selectBank) ).setOnClickListener(this);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.loginButton )
		{
			try
			{
				loginId = ( (EditText) findViewById(R.id.loginId) ).getText().toString();
				password = ( (EditText) findViewById(R.id.password) ).getText().toString();

				final Customer customer = new Customer();
				customer.setLoginId(loginId);
				customer.setPassword(password);

				final LoginService login = BankServiceFactory.getBankService(bankSelected, LoginService.class);
				login.setCustomer(customer);

				( new ServiceRunner(this, this, login, null) ).start();

				Log.d(TAG, "Progress dialog is over.");

			}
			catch ( final ServiceException e )
			{
				Log.e(TAG, "Failed to get list of Banks.", e);
			}
		}
		else if ( v.getId() == R.id.selectBank )
		{
			showDialog(BANK_SELECT_DIALOG);
		}
	}

	@Override
	protected Dialog onCreateDialog( final int id )
	{

		if ( id == BANK_SELECT_DIALOG )
		{

			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.bankSelect));
			final BankAdapter adapter = new BankAdapter(banks);
			adapter.setDummyAvailable(showDummyBank);
			builder.setAdapter(adapter, new DialogInterface.OnClickListener()
			{
				public void onClick( final DialogInterface dialog, final int item )
				{
					bankSelected = (Bank) adapter.getItem(item);

					updateBankSelect();
				}
			});

			return builder.create();
		}

		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog( final int id, final Dialog dialog )
	{
		super.onPrepareDialog(id, dialog);
		if ( id == BANK_SELECT_DIALOG )
		{
			final BankAdapter adapter = (BankAdapter) ( (AlertDialog) dialog ).getListView().getAdapter();
			adapter.setDummyAvailable(showDummyBank);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if ( SessionManager.getInstance().getSession() == null )
			SessionManager.getInstance().setSession(this, null); // reset session to make sure that the notification is hidden.

		//init bank select
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		showDummyBank = preferences.getBoolean(PREF_SHOW_DUMMY_BANK, false);
		try
		{
			bankSelected = getBankById(preferences.getString(PREF_LAST_BANK, ""));
			if ( bankSelected == null )
				bankSelected = getDefaultBank();
		}
		catch ( final Exception e )
		{
			Log.e(TAG, "Unable to get bank preference.", e);
			bankSelected = getDefaultBank();
		}
		updateBankSelect();

		//load login ID from preferences
		loginId = preferences.getString(PREF_LAST_LOGINID, DEFAULT_LOGINID);
		( (TextView) findViewById(R.id.loginId) ).setText(loginId);
		password = preferences.getString(PREF_LAST_PASSWORD, DEFAULT_PASSWORD);
		( (TextView) findViewById(R.id.password) ).setText(password);
	}

	private Bank getBankById( final String string )
	{
		for ( final Bank bank : banks )
		{
			if ( bank.getId().equals(string) )
				return bank;
		}
		return null;
	}

	private void updateBankSelect()
	{
		final Button bankSelect = (Button) findViewById(R.id.selectBank);
		bankSelect.setText(bankSelected.getName());
		final Drawable icon = PluginManager.getIconDrawable(bankSelected.getLargeIcon());
		bankSelect.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		bankSelect.setCompoundDrawablePadding(10);
	}

	private Bank getDefaultBank()
	{

		for ( final Bank bank : banks )
		{
			if ( bank.getId().equals(DUMMY_BANK_ID) && !showDummyBank )
				continue;
			return bank;
		}
		throw new IllegalStateException(getString(R.string.errNoConnector));
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof LoginService )
		{
			SessionManager.getInstance().setSession(this, ( (LoginService) service ).getSession());

			//save last succesful login details into preferences
			final SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

			final Editor editor = preferences.edit();
			if ( preferences.getBoolean(PREF_SAVE_LAST_LOGIN, true) )
			{
				editor.putString(PREF_LAST_BANK, bankSelected.getId());
				editor.putString(PREF_LAST_LOGINID, loginId);
			}

			if ( preferences.getBoolean(PREF_SAVE_PASSWORD, false) )
			{
				editor.putString(PREF_LAST_PASSWORD, password);
			}
			editor.commit();

			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public boolean onKeyDown( final int keyCode, final KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}