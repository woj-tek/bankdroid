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

import com.csaba.connector.BankService;
import com.csaba.connector.model.Account;
import com.csaba.connector.service.LoginService;

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

		//FIXME remove dummy code
		final Account[] accounts = new Account[] { new Account(), new Account() };
		accounts[0].setName("Kovács Gizella");
		accounts[0].setNumber("17000019-12345678");
		accounts[1].setName("Kovácsné Szűcs Eufruzsina");
		accounts[1].setNumber("17000019-12345678");

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

		( (ListView) findViewById(R.id.accountList) ).setAdapter(adapter);

	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof LoginService )
		{
			//FIXME implement account select here
		}
	}

	@Override
	public void onItemClick( final AdapterView<?> view, final View itemView, final int position, final long id )
	{
		// FIXME start account select service here
		startActivityForResult(new Intent(this, AXAAccountPINActivity.class), REQUEST_LOGIN);

	}

}
