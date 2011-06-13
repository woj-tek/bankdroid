package bankdroid.start.auth;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.ServiceRunner;
import bankdroid.start.SessionManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankService;
import com.csaba.connector.axa.AXAAccountLoginService;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Customer;
import com.csaba.connector.model.Session;

public class AXAAccountPINActivity extends ServiceActivity implements OnCheckedChangeListener
{
	private static final char EMPTY = '*';
	private TextView[] digits;
	private final char[] pin = new char[] { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, };
	private int[] pinMask;
	private boolean pinMaskEnabled = true;
	private int pos = 0;
	private boolean pinWasRestored = false;

	private boolean saveCustomer = false;
	private SharedPreferences preferences;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setSessionOriented(false);
		setShowHomeMenu(false);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.auth_axa_accountpin);

		digits = new TextView[] {
				//
				(TextView) findViewById(R.id.digit1),//
				(TextView) findViewById(R.id.digit2),//
				(TextView) findViewById(R.id.digit3),//
				(TextView) findViewById(R.id.digit4),//
				(TextView) findViewById(R.id.digit5),// 
				(TextView) findViewById(R.id.digit6),//
		};

		( (CheckBox) findViewById(R.id.rememberPassword) ).setOnCheckedChangeListener(this);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if ( preferences.getBoolean(PREF_SAVE_LAST_LOGIN, true) )
		{
			saveCustomer = true;
		}
		else
		{
			findViewById(R.id.rememberPassword).setEnabled(false);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setResult(RESULT_CANCELED);

		// load stored pin if there is any
		final Intent intent = getIntent();
		if ( intent != null )
		{
			pinMask = intent.getIntArrayExtra(EXTRA_PINMASK);

			final Session session = SessionManager.getInstance().getSession();
			if ( session != null )
			{
				final Customer customer = session.getCustomer();
				if ( customer.isRemotePropertySet(RP_ACCOUNT_PIN) )
				{
					final Account selectedAccount = (Account) session
							.getRemoteProperty(com.csaba.connector.axa.Codes.RP_SELECTED_ACCOUNT);
					try
					{
						final JSONObject json = new JSONObject((String) customer.getRemoteProperty(RP_ACCOUNT_PIN));
						if ( json.has(selectedAccount.getNumber()) )
						{
							//PIN was stored before.
							final String pinString = json.getString(selectedAccount.getNumber());
							pinString.getChars(0, pin.length, pin, 0);
							pos = pin.length;
							pinWasRestored = true;
						}
					}
					catch ( final JSONException e )
					{
						Log.e(TAG, "Cannot restore valid stored PIN. Acts as no pin was stored.", e);
					}
				}
			}
		}

		updateDigits();
	}

	@Override
	public void onCheckedChanged( final CompoundButton buttonView, final boolean isChecked )
	{
		pinMaskEnabled = !isChecked;
		pinWasRestored = false;

		resetPIN();

		updateDigits();
	}

	private void resetPIN()
	{
		for ( int i = 0; i < pin.length; i++ )
		{
			pin[i] = EMPTY;
		}
		pos = 0;
	}

	private void updateDigits()
	{
		final int len = digits.length;

		//check pos first if valid
		if ( isMasked(pos) )
		{
			//find forward
			do
			{
				pos++;
			}
			while ( pos < pin.length && isMasked(pos) );
			if ( isMasked(pos) )
			{
				//find backward
				do
				{
					pos--;
				}
				while ( pos > 0 && isMasked(pos) );

			}

		}

		for ( int i = 0; i < len; i++ )
		{
			String text = "_";
			int bkg = R.drawable.pin_normal;
			if ( isMasked(i) )
			{
				bkg = R.drawable.pin_disabled;
				text = " ";
				pin[i] = EMPTY;
			}
			else if ( pos == i || ( i == len - 1 && pos == len ) ) // if pos is over the last
			{
				bkg = R.drawable.pin_active;
			}
			digits[i].setBackgroundResource(bkg);

			if ( pin[i] != EMPTY )
				text = "*";
			digits[i].setText(text);
		}
	}

	private boolean isMasked( int i )
	{
		if ( pinMaskEnabled )
		{
			if ( i >= pin.length )
				i = pin.length - 1;

			for ( int j = 0; j < pinMask.length; j++ )
			{
				if ( pinMask[j] == i )
					return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public void onNumberPressed( final View v )
	{
		if ( pos < digits.length )
		{
			final char c = v.getTag().toString().charAt(0);
			pin[pos] = c;

			//move to next valid character
			do
			{
				pos++;
			}
			while ( pos < digits.length && isMasked(pos) );
			updateDigits();
		}

	}

	public void onDelete( final View v )
	{
		if ( pos > 0 )
		{
			if ( pos < pin.length && pin[pos] != EMPTY && !isMasked(pos) )
			{
				//do not move
			}
			else
			{
				//move to previous valid character
				do
				{
					pos--;
				}
				while ( pos > 0 && isMasked(pos) );
			}

			pin[pos] = EMPTY;
			updateDigits();
		}
	}

	public void onOk( final View v )
	{
		//check validity
		boolean ready = true;
		for ( int i = 0; i < pin.length; i++ )
		{
			if ( pin[i] == EMPTY && !isMasked(i) )
			{
				ready = false;
			}
		}
		if ( !ready )
		{
			Toast.makeText(this, "PIN must be fully specified first.", Toast.LENGTH_SHORT); //FIXME i18n
		}

		final char[] localPin = new char[pin.length];
		System.arraycopy(pin, 0, localPin, 0, pin.length);

		if ( !pinMaskEnabled )
		{
			//apply pin mask
			for ( int i = 0; i < localPin.length; i++ )
			{
				boolean toBeClear = true;
				for ( int j = 0; j < pinMask.length; j++ )
				{
					if ( pinMask[j] == i )
					{
						toBeClear = false;
						break;
					}
				}
				if ( toBeClear )
				{
					localPin[i] = EMPTY;
				}
			}
		}
		Log.d(TAG, "PIN: " + new String(localPin)); //FIXME remove

		//start account login
		final AXAAccountLoginService accountLogin = new AXAAccountLoginService();
		accountLogin.setPinDigits(localPin);
		( new ServiceRunner(this, this, accountLogin, SessionManager.getInstance().getSession()) ).start();
	}

	@Override
	public void onServiceFinished( final BankService service )
	{
		super.onServiceFinished(service);

		if ( service instanceof AXAAccountLoginService )
		{
			if ( !pinMaskEnabled && saveCustomer && !pinWasRestored )
			{
				//save pin in secure registry
				try
				{
					final Session session = SessionManager.getInstance().getSession();

					final Customer customer = session.getCustomer();

					// get previous pin array
					JSONObject json = null;
					if ( customer.isRemotePropertySet(RP_ACCOUNT_PIN) )
						json = new JSONObject((String) customer.getRemoteProperty(RP_ACCOUNT_PIN));
					else
						json = new JSONObject();

					//add new pin
					final Account selectedAccount = (Account) session
							.getRemoteProperty(com.csaba.connector.axa.Codes.RP_SELECTED_ACCOUNT);
					json.put(selectedAccount.getNumber(), new String(pin));

					customer.setRemoteProperty(RP_ACCOUNT_PIN, json.toString());

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

			setResult(RESULT_OK);
			finish();
		}
	}

}
