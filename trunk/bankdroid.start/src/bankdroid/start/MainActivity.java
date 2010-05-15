package bankdroid.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import bankdroid.start.plugin.PluginManager;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Session;

public class MainActivity extends ServiceActivity implements OnClickListener
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);

		( (Button) findViewById(R.id.logoutButton) ).setOnClickListener(this);
		( (Button) findViewById(R.id.accountButton) ).setOnClickListener(this);
		( (Button) findViewById(R.id.quickHistoryButton) ).setOnClickListener(this);
		( (Button) findViewById(R.id.searchTransactionButton) ).setOnClickListener(this);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.logoutButton )
		{
			SessionManager.getInstance().logout(this);
		}
		else if ( v.getId() == R.id.accountButton )
		{
			startActivity(new Intent(getApplicationContext(), AccountListActivity.class));
		}
		else if ( v.getId() == R.id.searchTransactionButton )
		{
			startActivity(new Intent(getApplicationContext(), SearchTransactionActivity.class));
		}
		else if ( v.getId() == R.id.quickHistoryButton )
		{
			final Intent tranList = new Intent(getApplicationContext(), TransactionListActivity.class);
			tranList.putExtra(EXTRA_TRANSACTION_FILTER, TransactionFilter.getDefaultFilter());
			startActivity(tranList);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		final Session session = SessionManager.getInstance().getSession();
		if ( session == null )
			return;

		( (TextView) findViewById(R.id.customerName) ).setText(session.getCustomer().getName());
		( (ImageView) findViewById(R.id.bankLogo) ).setImageDrawable(PluginManager.getIconDrawable(session.getBank()
				.getLargeIcon()));
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		finish();
	}
}
