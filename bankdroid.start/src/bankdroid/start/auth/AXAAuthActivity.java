package bankdroid.start.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
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
import com.csaba.connector.axa.model.AXABank;
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.LoginService;

public class AXAAuthActivity extends ServiceActivity
{
	private final Bank bankSelected = AXABank.getInstance();

	private Customer customer;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		setContentView(R.layout.auth_axa_password);

		AuthUtil.setSelectedBank(this, bankSelected);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);

		//load login ID from preferences
		customer = null;

		//load login ID from preferences
		final Intent intent = getIntent();
		if ( intent != null )
		{
			customer = (Customer) intent.getSerializableExtra(EXTRA_CUSTOMER);
		}

		if ( customer != null )
		{
			final String loginId = customer.getLoginId();
			final String password = customer.getPassword();

			( (TextView) findViewById(R.id.loginId) ).setText(customer.getLoginId());
			( (TextView) findViewById(R.id.password) ).setText(customer.getPassword());

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
		else
		{
			findViewById(R.id.loginId).requestFocus();
		}
	}

	public void onLogin( final View v )
	{
		try
		{
			final String loginId = ( (EditText) findViewById(R.id.loginId) ).getText().toString();
			final String password = ( (EditText) findViewById(R.id.password) ).getText().toString();

			final Customer customer = new Customer();
			customer.setLoginId(loginId);
			customer.setPassword(password);

			final LoginService login = BankServiceFactory.getBankService(bankSelected, LoginService.class);
			login.setCustomer(customer);

			( new ServiceRunner(this, this, login, null) ).start();
		}
		catch ( final ServiceException e )
		{
			GUIUtil.fatalError(this, e);
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

			//save last successful login details into preferences
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

			if ( preferences.getBoolean(PREF_SAVE_LAST_LOGIN, true) )
			{
				try
				{
					final SecureRegistry registry = SecureRegistry.getInstance(this);

					final Customer customer = session.getCustomer();
					int registryId = -1;
					if ( this.customer != null )
					{
						customer.copyRemoteProperty(this.customer);
						if ( customer.isRemotePropertySet(RP_REGISTRY_ID) )
							registryId = (Integer) customer.getRemoteProperty(RP_REGISTRY_ID);

					}
					AuthUtil.storeCustomer(registry, registryId, customer, new String[] { RP_ACCOUNT_PIN,
							RP_SELECTED_ACCOUNT }, ( (CheckBox) findViewById(R.id.rememberPassword) ).isChecked());

					registry.commit(this);
				}
				catch ( final Exception e )
				{
					GUIUtil.fatalError(this, e);
				}
			}

			startActivityForResult(new Intent(this, AXASMSOTPActivity.class), REQUEST_LOGIN);
		}
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( resultCode == RESULT_OK )
		{
			setResult(RESULT_OK);
			finish();
		}
	}

}
