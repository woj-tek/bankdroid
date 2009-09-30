package bankdroid.start;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import bankdroid.start.ServiceRunner.ServiceListener;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ClassEnumerationProvider;
import com.csaba.connector.ServiceException;
import com.csaba.connector.ServicePluginConfiguration;
import com.csaba.connector.dummy.DummyPluginConfiguration;
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;
import com.csaba.connector.service.LoginService;

public class StartActivity extends Activity implements OnClickListener, Codes, ServiceListener
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

				final String loginId = ( (EditText) findViewById(R.id.loginIdField) ).getText().toString();
				final String password = ( (EditText) findViewById(R.id.passwordField) ).getText().toString();

				final Customer customer = new Customer();
				customer.setLoginId(loginId);
				customer.setPassword(password);

				final LoginService login = BankServiceFactory.getBankService(banks[0], LoginService.class);
				login.setCustomer(customer);

				( new ServiceRunner(getBaseContext(), this, login, null) ).start();

				Log.d(TAG, "Progress dialog is over.");

			}
			catch ( final ServiceException e )
			{
				Log.e(TAG, "Failed to get list of Banks.", e);
			}
		}
	}

	@Override
	public void onServiceFailed( final BankService service, final Throwable tr )
	{
		Log.e(TAG, "Login failed.", tr);
		final Toast toast = Toast.makeText(getBaseContext(), "Service failed: " + tr.getMessage(), Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		Log.i(TAG, "Login was succesful.");
		final Toast toast = Toast.makeText(getBaseContext(), "Service finished.", Toast.LENGTH_SHORT);
		toast.show();
	}
}