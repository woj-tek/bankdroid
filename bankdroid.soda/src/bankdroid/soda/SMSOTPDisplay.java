package bankdroid.soda;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class SMSOTPDisplay extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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
			final Serializable timestampSource = intent.getSerializableExtra(SMSReceiver.BANKDROID_SODA_SMSTIMESTAMP);

			if ( timestampSource != null )
			{
				final String smsCode = intent.getStringExtra(SMSReceiver.BANKDROID_SODA_SMSCODE);
				final Bank source = (Bank) intent.getSerializableExtra(SMSReceiver.BANKDROID_SODA_BANK);
				final CharSequence timestampText = Formatters.getTimstampFormat().format((Date) timestampSource);

				( (ImageView) findViewById(R.id.BankLogo) ).setImageDrawable(getResources().getDrawable(
						source.getIconId()));
				( (TextView) findViewById(R.id.OTPView) ).setText(smsCode);
				( (TextView) findViewById(R.id.ReceivedAt) ).setText(getResources().getText(R.string.received_prefix)
						.toString()
						+ timestampText);

				//FIXME handle count down
				return;
			}
		}

		//no code available
		( (ImageView) findViewById(R.id.BankLogo) ).setImageDrawable(null);
		( (TextView) findViewById(R.id.OTPView) ).setText(getResources().getText(R.string.nocode).toString());
		( (TextView) findViewById(R.id.ReceivedAt) ).setText("");
	}
}