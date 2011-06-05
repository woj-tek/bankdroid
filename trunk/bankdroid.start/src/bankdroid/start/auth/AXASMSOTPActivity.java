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

import com.csaba.connector.BankService;
import com.csaba.connector.axa.model.AXABank;
import com.csaba.connector.model.Bank;
import com.csaba.connector.service.LoginService;

public class AXASMSOTPActivity extends ServiceActivity
{
	private final Bank bankSelected = AXABank.getInstance();

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		setContentView(R.layout.auth_axa_smsotp);

		AuthUtil.setSelectedBank(this, bankSelected);

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
		startActivityForResult(new Intent(this, AXAAccountActivity.class), REQUEST_LOGIN);

		/*
		try
		{
			//FIXME implement SMSOTP validation
		}
		catch ( final ServiceException e )
		{
			GUIUtil.fatalError(this, e);
		}*/
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof LoginService )
		{
			//FIXME implement succesful SMS OTP validation
		}
	}

}
