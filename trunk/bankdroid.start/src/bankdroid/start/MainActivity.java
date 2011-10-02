package bankdroid.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bankdroid.start.plugin.PluginManager;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Session;
import com.csaba.connector.service.LogoutService;

/**
 * @author Gabe
 *
 */
public class MainActivity extends ServiceActivity implements OnClickListener
{
	private boolean openBrowserAfterLogout = false;
	private String bankUrl = null;

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
			startActivityForResult(new Intent(getApplicationContext(), AccountListActivity.class), REQUEST_OTHER);
		}
		else if ( v.getId() == R.id.searchTransactionButton )
		{
			startActivityForResult(new Intent(getApplicationContext(), SearchTransactionActivity.class), REQUEST_OTHER);
		}
		else if ( v.getId() == R.id.quickHistoryButton )
		{
			final Intent tranList = new Intent(getApplicationContext(), TransactionListActivity.class);
			tranList.putExtra(EXTRA_TRANSACTION_FILTER, TransactionFilter.getDefaultFilter());
			startActivityForResult(tranList, REQUEST_OTHER);
		}
		else if ( v.getId() == R.id.customerProfileButton )
		{
			//open customer profile view
			final Intent intent = new Intent(getBaseContext(), PropertyViewActivity.class);

			intent.putExtra(EXTRA_PROPERTIES,
					PropertyHelper.getProperties(this, SessionManager.getInstance().getSession().getCustomer()));
			intent.putExtra(EXTRA_ACTIVITY_TITLE, getString(R.string.customerDetailTitle));
			intent.putExtra(EXTRA_ANALYTICS_ACTION, "shareCustomerDetails");
			intent.putExtra(EXTRA_SHARE_SUBJECT, getString(R.string.shareCustomerSubject));
			intent.putExtra(EXTRA_SHARE_BODY_TOP, getString(R.string.shareCustomerBodyTop));
			startActivityForResult(intent, REQUEST_OTHER);
		}
	}

	public void onToBrowser( final View v )
	{
		bankUrl = SessionManager.getInstance().getSession().getBank().getMobileBankURL();
		openBrowserAfterLogout = true;

		SessionManager.getInstance().logout(this);
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		if ( service instanceof LogoutService )
		{
			if ( openBrowserAfterLogout )
			{
				openBrowserAfterLogout = false;
				final Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(bankUrl));
				startActivity(intent);
			}
		}
		super.onServiceFinished(service);
	}

	public void onCallBank( final View v )
	{
		try
		{
			final Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + SessionManager.getInstance().getSession().getBank().getCallCenterURL()));
			startActivity(intent);
		}
		catch ( final Exception e )
		{
			Log.e(TAG, "Failed to dial bank's phone number: " + e);
			Toast.makeText(this, R.string.msgNoDialer, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		final Session session = SessionManager.getInstance().getSession();
		if ( session == null )
			return;

		( (TextView) findViewById(R.id.welcome) ).setText(getResources().getString(R.string.msgWelcome,
				session.getCustomer().getName()));
		( (TextView) findViewById(R.id.description) ).setText(getResources().getString(R.string.msgWelcomeDesc,
				session.getBank().getName()));
		( (ImageView) findViewById(R.id.bankLogo) ).setImageDrawable(PluginManager.getIconDrawable(session.getBank()
				.getLargeIcon()));
	}

}
