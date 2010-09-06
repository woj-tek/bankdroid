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

import com.csaba.connector.model.Session;

/**
 * @author Gabe
 *
 */
public class MainActivity extends ServiceActivity implements OnClickListener
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);
		setShowHomeMenu(false);

		( (Button) findViewById(R.id.logoutButton) ).setOnClickListener(this);
		( (Button) findViewById(R.id.accountButton) ).setOnClickListener(this);
		( (Button) findViewById(R.id.quickHistoryButton) ).setOnClickListener(this);
		( (Button) findViewById(R.id.customerProfileButton) ).setOnClickListener(this);
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
		else if ( v.getId() == R.id.customerProfileButton )
		{
			//open customer profile view
			final Intent intent = new Intent(getBaseContext(), PropertyViewActivity.class);

			intent.putExtra(EXTRA_PROPERTIES, PropertyHelper.getProperties(this, SessionManager.getInstance()
					.getSession().getCustomer()));
			intent.putExtra(EXTRA_ACTIVITY_TITLE, getString(R.string.customerDetailTitle));
			startActivity(intent);
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

}
