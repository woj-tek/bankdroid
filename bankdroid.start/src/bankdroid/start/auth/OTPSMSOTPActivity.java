package bankdroid.start.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.ServiceRunner;
import bankdroid.start.SessionManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Customer;
import com.csaba.connector.model.Session;
import com.csaba.connector.otp.OTPSMSLoginService;
import com.csaba.connector.otp.model.OTPBank;

/**
 * @author Gabe
 *FIXME customer is not updated but set as a new customer in the registry
 */
public class OTPSMSOTPActivity extends ServiceActivity
{
	private boolean showCode = false;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		setContentView(R.layout.auth_otp_smsotp);

		AuthUtil.setSelectedBank(this, OTPBank.getInstance());

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
		if ( showCode )
		{
			onPaste(null);
		}
		else
		{
			showCode = true;
			//code is only shown when user comes to screen 2nd time.
		}
	}

	public void onSMSKey( final View v )
	{
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SMSKEY_MARKET)));
	}

	public void onPaste( final View v )
	{
		final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		if ( clipboard.hasText() )
		{
			final String content = clipboard.getText().toString();
			( (EditText) findViewById(R.id.smsotp) ).setText(content);
			clipboard.setText(null);
		}

	}

	public void onLogin( final View v )
	{
		final String smsotp = ( (EditText) findViewById(R.id.smsotp) ).getText().toString();

		final OTPSMSLoginService service = new OTPSMSLoginService();

		service.setSmsCode(smsotp);

		( new ServiceRunner(this, this, service, SessionManager.getInstance().getSession()) ).start();
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof OTPSMSLoginService )
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
					final SecureRegistry registry = SecureRegistry.getInstance(this);
					if ( AuthUtil.isCustomerSaved(registry, registryId) )
					{

						//store to registry
						AuthUtil.storeCustomer(registry, registryId, customer, new String[] { RP_ACCOUNT_PIN },
								(Boolean) customer.getRemoteProperty(RP_STORE_PASSWORD));
						registry.commit(this);
					}
				}
				catch ( final Exception e )
				{
					GUIUtil.fatalError(this, e);
				}
			}

			setResult(RESULT_OK);
			finish();
		}
	}

}
