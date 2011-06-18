package bankdroid.start.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.ServiceRunner;
import bankdroid.start.SessionManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.axa.AXASMSOTPValidationService;
import com.csaba.connector.axa.model.AXABank;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Customer;
import com.csaba.connector.model.Session;

public class AXASMSOTPActivity extends ServiceActivity
{
	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		setContentView(R.layout.auth_axa_smsotp);

		AuthUtil.setSelectedBank(this, AXABank.getInstance());

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);

		//load login ID from preferences
		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if ( imm != null )
		{
			imm.showSoftInput(findViewById(R.id.smsotp), 0);
		}

		//check for clipboard content
		onPaste(null);
	}

	public void onSMSKey( final View v )
	{
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SMSKEY_BLOG_HOME)));
	}

	public void onPaste( final View v )
	{
		final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		if ( clipboard.hasText() )
		{
			final String content = clipboard.getText().toString();
			if ( content.length() != 6 )
				Toast.makeText(this, R.string.axaInvalidOTP, Toast.LENGTH_SHORT).show();
			else
			{
				( (EditText) findViewById(R.id.smsotp) ).setText(content);
				clipboard.setText(null);
			}

		}

	}

	public void onLogin( final View v )
	{
		final String smsotp = ( (EditText) findViewById(R.id.smsotp) ).getText().toString();

		final AXASMSOTPValidationService service = new AXASMSOTPValidationService();

		service.setSmsotp(smsotp);

		( new ServiceRunner(this, this, service, SessionManager.getInstance().getSession()) ).start();
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof AXASMSOTPValidationService )
		{
			//update name of customer
			final Session session = SessionManager.getInstance().getSession();
			if ( session != null )
			{
				try
				{
					final Customer customer = session.getCustomer();
					//get registry key
					int registryId = -1;
					if ( customer.isRemotePropertySet(RP_REGISTRY_ID) )
						registryId = (Integer) customer.getRemoteProperty(RP_REGISTRY_ID);

					//store to registry
					final SecureRegistry registry = SecureRegistry.getInstance(this);
					AuthUtil.storeCustomer(registry, registryId, customer, new String[] { RP_ACCOUNT_PIN },
							(Boolean) customer.getRemoteProperty(RP_STORE_PASSWORD));
					registry.commit(this);
				}
				catch ( final Exception e )
				{
					GUIUtil.fatalError(this, e);
				}
			}

			//start account select activity
			final Account[] accounts = ( (AXASMSOTPValidationService) service ).getAccounts();

			final Intent intent = new Intent(this, AXAAccountActivity.class);
			intent.putExtra(EXTRA_ACCOUNT_LIST, accounts);
			startActivityForResult(intent, REQUEST_LOGIN);
		}
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( resultCode == RESULT_OK )
		{
			setResult(RESULT_OK);
			finish();
		}
		else
			super.onActivityResult(requestCode, resultCode, data);
	}

}
