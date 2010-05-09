package bankdroid.start;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
 *	TODO make plugins for various login screens
 *	TODO enable different flow for login (to support SMS OTP)
 *  TODO handle saved users
 *  TODO take care of session timeout 
 *  TODO take care of exiting the application
 */
public class StartActivity extends ServiceActivity implements OnClickListener
{
	private static final int BANK_SELECT_DIALOG = 999122;
	private Bank[] banks;
	private int bankIndex = 0;
	private String loginId = "local";
	private String password = "password";

	/** Called when the activity is first created. */
	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		PluginManager.init();
		try
		{
			banks = BankServiceFactory.getAvailableBanks();
			for ( final Bank bank : banks )
			{
				Log.d(TAG, "Bank found: " + bank.getId());
			}
		}
		catch ( final ServiceException e )
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

				final LoginService login = BankServiceFactory.getBankService(banks[bankIndex], LoginService.class);
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
			builder.setTitle("Select a bank"); //FIXME I18N
			builder.setAdapter(new BankAdapter(banks), new DialogInterface.OnClickListener()
			{
				public void onClick( final DialogInterface dialog, final int item )
				{
					bankIndex = item;

					updateBankSelect();
				}
			});

			return builder.create();
		}

		return super.onCreateDialog(id);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		//init bank select
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		bankIndex = preferences.getInt(PREF_LAST_BANK, bankIndex);
		updateBankSelect();

		//load login ID from preferences
		loginId = preferences.getString(PREF_LAST_LOGINID, DEFAULT_LOGINID);
		( (TextView) findViewById(R.id.loginId) ).setText(loginId);
		password = preferences.getString(PREF_LAST_PASSWORD, DEFAULT_PASSWORD);
		( (TextView) findViewById(R.id.password) ).setText(password);
	}

	private void updateBankSelect()
	{
		final Bank bank = banks[bankIndex];

		final Button bankSelect = (Button) findViewById(R.id.selectBank);
		bankSelect.setText(bank.getName());
		final Drawable icon = PluginManager.getIconDrawable(bank.getLargeIcon());
		bankSelect.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		bankSelect.setCompoundDrawablePadding(10);
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		SessionManager.getInstance().setSession(( (LoginService) service ).getSession());

		//save last succesful login details into preferences
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		final Editor editor = preferences.edit();
		if ( preferences.getBoolean(PREF_SAVE_LAST_LOGIN, true) )
		{
			editor.putInt(PREF_LAST_BANK, bankIndex);
			editor.putString(PREF_LAST_LOGINID, loginId);
		}

		if ( preferences.getBoolean(PREF_SAVE_PASSWORD, false) )
		{
			editor.putString(PREF_LAST_PASSWORD, password);
		}
		editor.commit();

		startActivity(new Intent(getBaseContext(), MainActivity.class));
	}
}