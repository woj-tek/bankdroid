package bankdroid.start.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.ServiceRunner;
import bankdroid.start.SessionManager;

import com.csaba.connector.BankService;
import com.csaba.connector.axa.AXASMSOTPValidationService;
import com.csaba.connector.axa.model.AXABank;
import com.csaba.connector.model.Account;

//FIXME save customer name on succesful result
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
	}

	public void onPaste( final View v )
	{
		final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		if ( clipboard.hasText() )
		{
			final String content = clipboard.getText().toString();
			( (EditText) findViewById(R.id.smsotp) ).setText(content);
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
