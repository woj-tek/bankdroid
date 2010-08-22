package bankdroid.start.auth;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
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

public class BHAAuthActivity extends ServiceActivity implements OnClickListener
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

		findViewById(R.id.password).setOnFocusChangeListener(new View.OnFocusChangeListener()
		{

			@Override
			public void onFocusChange( final View v, final boolean hasFocus )
			{
				if ( hasFocus )
				{
					final String loginId = ( (EditText) findViewById(R.id.loginId) ).getText().toString();
					final int authType = BHALoginService.detectAuthType(loginId);
					final EditText passwordField = (EditText) v;
					if ( authType == BHALoginService.AUTH_TYPE_TOKEN )
					{
						passwordField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
								BHALoginService.TOKEN_LENGTH) });
						passwordField
								.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					}
					else
					{
						passwordField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
								BHALoginService.PASSWORD_MAX_LENGTH) });
						passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					}
				}

			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		//load login ID from preferences
		final String loginId = preferences.getString(PREF_LAST_LOGINID, DEFAULT_LOGINID);
		( (TextView) findViewById(R.id.loginId) ).setText(loginId);
		final String password = preferences.getString(PREF_LAST_PASSWORD, DEFAULT_PASSWORD);
		( (TextView) findViewById(R.id.password) ).setText(password);

		//focus password, if login ID is saved, etc...
		if ( loginId.equals(DEFAULT_LOGINID) )
			findViewById(R.id.loginId).requestFocus();
		else if ( password.equals(DEFAULT_PASSWORD) )
			findViewById(R.id.password).requestFocus();
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
				GUIUtil.fatalError(this, e);
			}
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
			final SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

			final Editor editor = preferences.edit();
			if ( preferences.getBoolean(PREF_SAVE_LAST_LOGIN, true) )
			{
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
			}

			editor.commit();

			setResult(RESULT_OK);
			finish();
		}
	}

}
