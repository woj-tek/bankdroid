package bankdroid.start.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import bankdroid.start.Eula;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.plugin.PluginManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.model.Customer;

public class AuthStartActivity extends ServiceActivity implements OnClickListener, OnItemClickListener
{
	private final static int REQUEST_NEWUSER = 1001;

	private boolean onFirstDisplay = true;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		Eula.show(this);

		setSessionOriented(false);
		setShowHomeMenu(false);

		PluginManager.init();

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.authstart);

		( (ListView) findViewById(R.id.userList) ).setOnItemClickListener(this);
		findViewById(R.id.newUser).setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Log.d(TAG, "AuthStart resume");

		boolean hasUsers = false;
		try
		{
			final SecureRegistry registry = SecureRegistry.getInstance(this);

			final Customer[] customers = AuthUtil.restoreCustomers(registry);

			if ( customers != null && customers.length > 0 )
			{
				hasUsers = true;

				final ListView list = (ListView) findViewById(R.id.userList);
				list.setAdapter(new CustomerAdapter(customers));
			}
		}
		catch ( final Exception e )
		{
			GUIUtil.fatalError(this, e);
		}

		if ( onFirstDisplay && !hasUsers )
		{
			onFirstDisplay = false;
			createNewUser();
		}
	}

	private void createNewUser()
	{
		//FIXME handle this correctly
		startActivityForResult(new Intent(this, AuthBankSelectActivity.class), REQUEST_NEWUSER);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.newUser )
		{
			createNewUser();
		}
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( requestCode == REQUEST_NEWUSER && resultCode == RESULT_OK )
		{
			Log.d(TAG, "AuthStart - finish it well.");
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onItemClick( final AdapterView<?> view, final View itemView, final int position, final long id )
	{
		if ( view.getId() == R.id.userList )
		{
			final Customer cust = ( (CustomerAdapter) view.getAdapter() ).getCustomer(position);

			try
			{
				final Class<?> authActivityClass = PluginManager.getAuthActivityClass(cust.getBank());
				final Intent intent = new Intent(this, authActivityClass);
				intent.putExtra(EXTRA_CUSTOMER, cust);
				startActivityForResult(intent, REQUEST_NEWUSER);
			}
			catch ( final ClassNotFoundException e )
			{
				GUIUtil.fatalError(this, e);
			}
		}

	}
}
