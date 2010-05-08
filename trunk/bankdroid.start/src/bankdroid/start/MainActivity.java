package bankdroid.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.csaba.connector.BankService;
import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.LogoutService;

public class MainActivity extends ServiceActivity implements OnClickListener
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		( (Button) findViewById(R.id.logoutButton) ).setOnClickListener(this);
		( (Button) findViewById(R.id.accountButton) ).setOnClickListener(this);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.logoutButton )
		{
			try
			{
				final Session session = SessionManager.getInstance().getSession();

				final LogoutService logout = BankServiceFactory.getBankService(session.getBank(), LogoutService.class);

				( new ServiceRunner(this, this, logout, session) ).start();
			}
			catch ( final ServiceException e )
			{
				onServiceFailed(null, e);
			}
		}
		else if ( v.getId() == R.id.accountButton )
		{
			startActivity(new Intent(getApplicationContext(), AccountListActivity.class));
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		( (TextView) findViewById(R.id.customerName) ).setText(SessionManager.getInstance().getSession().getCustomer()
				.getName());
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		finish();
	}
}
