package bankdroid.start.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.ServiceRunner;
import bankdroid.start.SessionManager;

import com.csaba.connector.BankService;
import com.csaba.connector.axa.AXASelectAccountService;
import com.csaba.connector.model.Account;

//FIXME display warning on logout, handle back / home button properly
public class AXAAccountActivity extends ServiceActivity implements OnItemClickListener
{
	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		setContentView(R.layout.auth_axa_accounts);

		( (ListView) findViewById(R.id.accountList) ).setOnItemClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);

		final Intent intent = getIntent();
		if ( intent != null )
		{
			final Object[] objects = (Object[]) intent.getSerializableExtra(EXTRA_ACCOUNT_LIST);
			final Account[] accounts = new Account[objects.length];
			for ( int i = 0; i < accounts.length; i++ )
			{
				accounts[i] = (Account) objects[i];
			}

			final ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this, 0, accounts)
			{
				@Override
				public View getView( final int position, View convertView, final ViewGroup parent )
				{
					if ( convertView == null )
					{
						convertView = View.inflate(AXAAccountActivity.this, R.layout.auth_axa_accountitem, null);
					}
					final Account account = getItem(position);
					( (TextView) convertView.findViewById(R.id.accountName) ).setText(account.getName());
					( (TextView) convertView.findViewById(R.id.accountNumber) ).setText(account.getNumber());

					return convertView;
				}
			};

			final ListView list = (ListView) findViewById(R.id.accountList);
			list.setAdapter(adapter);
		}
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof AXASelectAccountService )
		{
			final int[] pinMask = ( (AXASelectAccountService) service ).getPinMask();
			final Intent intent = new Intent(this, AXAAccountPINActivity.class);
			intent.putExtra(EXTRA_PINMASK, pinMask);
			startActivityForResult(intent, REQUEST_LOGIN);
		}
	}

	@Override
	public void onItemClick( final AdapterView<?> view, final View itemView, final int position, final long id )
	{
		@SuppressWarnings( "unchecked" )
		final ArrayAdapter<Account> adapter = (ArrayAdapter<Account>) view.getAdapter();

		final Account account = adapter.getItem(position);
		final AXASelectAccountService sas = new AXASelectAccountService();
		sas.setAccount(account);
		( new ServiceRunner(this, this, sas, SessionManager.getInstance().getSession()) ).start();
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( requestCode == REQUEST_LOGIN && resultCode == RESULT_OK )
		{
			setResult(RESULT_OK);
			finish();
		}
		else if ( requestCode == REQUEST_LOGIN && resultCode == RESULT_CANCELED )
		{//do nothing, back button was pressed. another account can be selected
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

}
