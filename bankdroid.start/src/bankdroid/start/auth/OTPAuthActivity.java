package bankdroid.start.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
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
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;
import com.csaba.connector.model.Session;
import com.csaba.connector.otp.OTPLoginService;
import com.csaba.connector.otp.model.OTPBank;
import com.csaba.connector.service.LoginService;

public class OTPAuthActivity extends ServiceActivity implements OnClickListener
{
	private final Bank bankSelected = OTPBank.getInstance();

	private int registryId;
	private String loginId;
	private String password;
	private String accountNr1;
	private String accountNr2;
	private String accountNr3;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.auth_otp);

		AuthUtil.setSelectedBank(this, bankSelected);

		findViewById(R.id.loginButton).setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);

		loginId = "";
		password = "";
		registryId = -1;

		//load login ID from preferences
		final Intent intent = getIntent();
		if ( intent != null )
		{
			final Customer customer = (Customer) intent.getSerializableExtra(EXTRA_CUSTOMER);
			if ( customer != null )
			{
				registryId = (Integer) customer.getRemoteProperty(RP_REGISTRY_ID);
				//recover user IDs from encrypted stores
				loginId = customer.getLoginId();
				password = customer.getPassword();
				try
				{
					accountNr1 = (String) customer.getRemoteProperty(OTPLoginService.RP_ACCOUNT1);
					accountNr2 = (String) customer.getRemoteProperty(OTPLoginService.RP_ACCOUNT2);
					accountNr3 = (String) customer.getRemoteProperty(OTPLoginService.RP_ACCOUNT3);

					if ( accountNr1.startsWith(OTPLoginService.ACCOUNT_PREFIX) )
						accountNr1 = accountNr1.substring(OTPLoginService.ACCOUNT_PREFIX.length());
				}
				catch ( final Exception e )
				{
					GUIUtil.fatalError(this, e);
					final String[] names = customer.getRemotePropertyNames();
					for ( final String name : names )
					{
						Log.d(TAG, "Customer remote prop: " + name);
					}
				}
			}
		}

		( (TextView) findViewById(R.id.loginId) ).setText(loginId);
		( (TextView) findViewById(R.id.password) ).setText(password);
		( (TextView) findViewById(R.id.accountNumber1) ).setText(accountNr1);
		( (TextView) findViewById(R.id.accountNumber2) ).setText(accountNr2);
		( (TextView) findViewById(R.id.accountNumber3) ).setText(accountNr3);

		//focus password, if login ID is saved, etc...
		if ( loginId == null || loginId.length() < 1 )
		{
			findViewById(R.id.loginId).requestFocus();
		}
		else if ( password == null || password.length() < 1 )
		{
			findViewById(R.id.password).requestFocus();
		}
		else
		{ //password was saved
			( (CheckBox) findViewById(R.id.rememberPassword) ).setChecked(true);
			findViewById(R.id.loginButton).requestFocus();
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
				accountNr1 = ( (EditText) findViewById(R.id.accountNumber1) ).getText().toString();
				accountNr2 = ( (EditText) findViewById(R.id.accountNumber2) ).getText().toString();
				accountNr3 = ( (EditText) findViewById(R.id.accountNumber3) ).getText().toString();

				//XXX verify field length here

				final Customer customer = new Customer();
				customer.setLoginId(loginId);
				customer.setPassword(password);
				customer.setRemoteProperty(OTPLoginService.RP_ACCOUNT1, OTPLoginService.ACCOUNT_PREFIX + accountNr1);
				customer.setRemoteProperty(OTPLoginService.RP_ACCOUNT2, accountNr2);
				customer.setRemoteProperty(OTPLoginService.RP_ACCOUNT3, accountNr3);

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
			final Session session = ( (LoginService) service ).getSession();
			SessionManager.getInstance().setSession(this, session);

			//save last succesful login details into SecureRegistry
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

			if ( preferences.getBoolean(PREF_SAVE_LAST_LOGIN, true) )
			{
				try
				{
					final SecureRegistry registry = SecureRegistry.getInstance(this);

					AuthUtil.storeCustomer(registry, registryId, session.getCustomer(), new String[] {
							OTPLoginService.RP_ACCOUNT1, OTPLoginService.RP_ACCOUNT2, OTPLoginService.RP_ACCOUNT3 },
							( (CheckBox) findViewById(R.id.rememberPassword) ).isChecked());

					registry.commit(this);
				}
				catch ( final Exception e )
				{
					GUIUtil.fatalError(this, e);
				}
			}

			setResult(RESULT_OK);
			finish();
		}
	}
}
