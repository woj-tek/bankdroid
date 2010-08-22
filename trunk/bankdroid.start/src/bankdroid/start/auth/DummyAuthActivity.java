package bankdroid.start.auth;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.ServiceRunner;
import bankdroid.start.SessionManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.dummy.model.DummyBank;
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;
import com.csaba.connector.service.LoginService;

public class DummyAuthActivity extends ServiceActivity implements OnItemClickListener
{
	private final Bank bankSelected = DummyBank.getInstance();

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.auth_dummy);

		final ListView list = (ListView) findViewById(R.id.userList);
		list.setAdapter(new ArrayAdapter<String>(this, R.layout.onerow, getResources().getStringArray(
				R.array.dummyUserLabels)));
		list.setOnItemClickListener(this);

		AuthUtil.setSelectedBank(this, bankSelected);

	}

	@Override
	public void onItemClick( final AdapterView<?> adapter, final View view, final int position, final long id )
	{
		final String[] loginIds = getResources().getStringArray(R.array.dummyUserValues);
		final String loginId = loginIds[position];
		final String password = "password";

		try
		{
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
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof LoginService )
		{
			SessionManager.getInstance().setSession(this, ( (LoginService) service ).getSession());

			//do not save dummy user login

			setResult(RESULT_OK);
			finish();
		}
	}

}
