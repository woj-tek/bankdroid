package bankdroid.soda;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This view as able to display SMS one time passwords processed by {@link SMSReceiver}. Besides displayed the codes
 * it provides several convieniences services:
 * <ul>
 * <li>Display code in large letters for the better readability</li>
 * <li>display a copy button to copy the code into the clipboard. In this way it is easy to 
 * 		copy and paste it into the appropriate field in the Browser</li>
 * <li>TODO: display list of banks and their settings</li>
 * <li>TODO: let the user to register new banks, store settings in DB</li>
 * <li>TODO: let the user to post the bank settings to the bankdroid@googlecode.com</li>
 * <li>TODO: handle preferences: clear SMS, how many SMS to be stored per bank</li>
 * <li>TODO: displays a count-down to indicate when the OTP will expire</li>
 * </ul>
 * 
 * @author user
 *
 */
public class SMSOTPDisplay extends Activity implements View.OnClickListener
{
	private CharSequence displayedCode;

	/** Called when the activity is first created. */
	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		( (ImageButton) findViewById(R.id.Copy) ).setOnClickListener(this);

		final Calendar current = Calendar.getInstance();
		final String timestamp = Formatters.getTimstampFormat().format(current.getTime());
		( (TextView) findViewById(R.id.ReceivedAt) ).setText(timestamp);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		final Intent intent = getIntent();
		if ( intent != null )
		{
			processIntent(intent);
		}
		else
		{
			clearFields();
		}
	}

	private void clearFields()
	{
		//no code available
		( (ImageButton) findViewById(R.id.Copy) ).setEnabled(false);
		( (ImageView) findViewById(R.id.BankLogo) ).setImageDrawable(null);
		( (TextView) findViewById(R.id.OTPView) ).setText(getResources().getText(R.string.nocode).toString());
		( (TextView) findViewById(R.id.ReceivedAt) ).setText("");
		displayedCode = null;

	}

	private void processIntent( final Intent intent )
	{
		final Serializable timestampSource = intent.getSerializableExtra(SMSReceiver.BANKDROID_SODA_SMSTIMESTAMP);

		if ( timestampSource != null )
		{
			final String smsCode = intent.getStringExtra(SMSReceiver.BANKDROID_SODA_SMSCODE);
			displayedCode = smsCode;
			final Bank source = (Bank) intent.getSerializableExtra(SMSReceiver.BANKDROID_SODA_BANK);
			final CharSequence timestampText = Formatters.getTimstampFormat().format((Date) timestampSource);
			Log.i("SODA", "One time password to display from Bank = " + source.getId());

			( (ImageView) findViewById(R.id.BankLogo) )
					.setImageDrawable(getResources().getDrawable(source.getIconId()));
			( (TextView) findViewById(R.id.OTPView) ).setText(smsCode);
			( (TextView) findViewById(R.id.ReceivedAt) ).setText(getResources().getText(R.string.received_prefix)
					.toString()
					+ timestampText);
			( (ImageButton) findViewById(R.id.Copy) ).setEnabled(true);
		}
		else
		{
			Log.i("SODA", "SMS received, but no bank code inside.");
			clearFields();
		}
	}

	@Override
	protected void onNewIntent( final Intent intent )
	{
		super.onNewIntent(intent);
		setIntent(intent);

	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.Copy )
		{
			if ( displayedCode != null )
				( (ClipboardManager) getSystemService(CLIPBOARD_SERVICE) ).setText(displayedCode);
		}

	}
}