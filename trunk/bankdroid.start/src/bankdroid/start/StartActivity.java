package bankdroid.start;

import java.util.HashSet;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ClassEnumerationProvider;
import com.csaba.connector.ServiceException;
import com.csaba.connector.ServicePluginConfiguration;
import com.csaba.connector.dummy.DummyPluginConfiguration;
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;
import com.csaba.connector.service.LoginService;

public class StartActivity extends ServiceActivity implements OnClickListener
{
	static
	{
		final Set<ServicePluginConfiguration> plugins = new HashSet<ServicePluginConfiguration>();
		//plugins.add(new BHAPluginConfiguration());
		plugins.add(new DummyPluginConfiguration());

		BankServiceFactory.setProvider(new ClassEnumerationProvider(plugins));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		( (Button) findViewById(R.id.loginButton) ).setOnClickListener(this);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.loginButton )
		{
			try
			{
				final Bank[] banks = BankServiceFactory.getAvailableBanks();
				for ( final Bank bank : banks )
				{
					Log.d(TAG, "Bank found: " + bank.getId());
				}

				final String loginId = ( (EditText) findViewById(R.id.loginId) ).getText().toString();
				final String password = ( (EditText) findViewById(R.id.password) ).getText().toString();

				final Customer customer = new Customer();
				customer.setLoginId(loginId);
				customer.setPassword(password);

				final LoginService login = BankServiceFactory.getBankService(banks[0], LoginService.class);
				login.setCustomer(customer);

				( new ServiceRunner(this, this, login, null) ).start();

				Log.d(TAG, "Progress dialog is over.");

			}
			catch ( final ServiceException e )
			{
				Log.e(TAG, "Failed to get list of Banks.", e);
			}
		}
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		SessionManager.getInstance().setSession(( (LoginService) service ).getSession());

		startActivity(new Intent(getBaseContext(), MainActivity.class));
	}
}