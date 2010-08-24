package bankdroid.start.auth;

import android.os.Bundle;
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
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;
import com.csaba.connector.otp.OTPLoginService;
import com.csaba.connector.otp.model.OTPBank;
import com.csaba.connector.service.LoginService;

public class OTPAuthActivity extends ServiceActivity implements OnClickListener
{
	private final Bank bankSelected = OTPBank.getInstance();

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
		}
		else if ( password.length() < 1 )
		{
			findViewById(R.id.password).requestFocus();
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

				//FIXME verify field length here

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
