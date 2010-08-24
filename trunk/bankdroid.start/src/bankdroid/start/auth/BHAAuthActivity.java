package bankdroid.start.auth;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.ServiceRunner;
import bankdroid.start.SessionManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.bha.BHALoginService;
import com.csaba.connector.bha.model.BHABank;
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;
import com.csaba.connector.service.LoginService;

public class BHAAuthActivity extends ServiceActivity implements OnClickListener, OnFocusChangeListener
{
	private final Bank bankSelected = BHABank.getInstance();

	private String loginId;
	private String password;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.auth_bha);

		AuthUtil.setSelectedBank(this, bankSelected);

		findViewById(R.id.loginButton).setOnClickListener(this);

		findViewById(R.id.password).setOnFocusChangeListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);

		//load login ID from preferences

		//FIXME recover user IDs from encrypted stores
		final String loginId = "";
		( (TextView) findViewById(R.id.loginId) ).setText(loginId);
		final String password = "";
		( (TextView) findViewById(R.id.password) ).setText(password);

		//focus password, if login ID is saved, etc...
		if ( loginId.length() < 1 )
		{
			findViewById(R.id.loginId).requestFocus();
			setAuthType(BHALoginService.AUTH_TYPE_UNKNOWN);
		}
		else if ( password.length() < 1 )
		{
			findViewById(R.id.password).requestFocus();
			setAuthType(BHALoginService.detectAuthType(loginId));
		}
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

				//FIXME verify field length here

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
				GUIUtil.fatalError(this, e);
			}
		}
	}

	@Override
	public void onFocusChange( final View v, final boolean hasFocus )
	{
		if ( hasFocus )
		{
			final String loginId = ( (EditText) findViewById(R.id.loginId) ).getText().toString();
			setAuthType(BHALoginService.detectAuthType(loginId));
		}
	}

	private void setAuthType( final int authType )
	{
		final EditText passwordField = (EditText) findViewById(R.id.password);
		final CheckBox rememberPassword = (CheckBox) findViewById(R.id.rememberPassword);
		if ( authType == BHALoginService.AUTH_TYPE_TOKEN )
		{
			passwordField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(BHALoginService.TOKEN_LENGTH) });
			passwordField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			rememberPassword.setChecked(false);
			rememberPassword.setEnabled(false);
		}
		else
		{
			passwordField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					BHALoginService.PASSWORD_MAX_LENGTH) });
			passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			rememberPassword.setEnabled(true);
		}
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof LoginService )
		{
			SessionManager.getInstance().setSession(this, ( (LoginService) service ).getSession());

			//save last succesful login details into preferences
			/*FIXME store identifiers in the ecryption store
			final SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

			if ( preferences.getBoolean(PREF_SAVE_LAST_LOGIN, true) )
			{
				final Editor editor = preferences.edit();
				editor.putString(PREF_LAST_BANK, bankSelected.getId());
				editor.putString(PREF_LAST_LOGINID, loginId);
				if ( preferences.getBoolean(PREF_SAVE_PASSWORD, false) )
				{
					if ( BHALoginService.detectAuthType(loginId) != BHALoginService.AUTH_TYPE_TOKEN )
					{
						editor.putString(PREF_LAST_PASSWORD, password);
					}
					else
					{
						editor.remove(PREF_LAST_PASSWORD);
					}
				}
				editor.commit();
			}
			*/

			setResult(RESULT_OK);
			finish();
		}
	}

}
