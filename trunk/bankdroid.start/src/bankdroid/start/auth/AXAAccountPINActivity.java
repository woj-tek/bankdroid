package bankdroid.start.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;

import com.csaba.connector.BankService;
import com.csaba.connector.axa.model.AXABank;
import com.csaba.connector.model.Bank;
import com.csaba.connector.service.LoginService;

public class AXAAccountPINActivity extends ServiceActivity implements OnEditorActionListener
{
	private final Bank bankSelected = AXABank.getInstance();
	private EditText[] digits;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.auth_axa_accountpin);

		AuthUtil.setSelectedBank(this, bankSelected);

		digits = new EditText[] {
				//
				(EditText) findViewById(R.id.digit1),//
				(EditText) findViewById(R.id.digit2),//
				(EditText) findViewById(R.id.digit3),//
				(EditText) findViewById(R.id.digit4),//
				(EditText) findViewById(R.id.digit5),// 
				(EditText) findViewById(R.id.digit6),//

		};

		for ( final EditText digit : digits )
		{
			digit.setOnEditorActionListener(this);
		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);

	}

	public void onLogin( final View v )
	{
		/*try
		{
			//FIXME implement login
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
		{//FIXME implement login
		}
	}

	@Override
	public boolean onEditorAction( final TextView v, final int actionId, final KeyEvent event )
	{
		// FIXME navigate properly between digits
		Log.d(TAG, "Action " + actionId + " occured with key event: " + event);
		return false;
	}

}
