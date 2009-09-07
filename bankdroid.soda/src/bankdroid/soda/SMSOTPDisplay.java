package bankdroid.soda;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

		( (Button) findViewById(R.id.CloseButton) ).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick( final View arg0 )
			{
				finish();
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if ( getIntent() != null )
		{
			final Serializable timestampSource = getIntent().getSerializableExtra(
					SMSReceiver.BANKDROID_SOD_SMSTIMESTAMP);
			final CharSequence text = getIntent().getCharSequenceExtra(SMSReceiver.BANKDROID_SOD_SMSMESSAGE);

			if ( timestampSource != null && text != null )
			{
				final CharSequence timestampText = Formatters.getTimstampFormat().format((Date) timestampSource);

				( (TextView) findViewById(R.id.OTPView) ).setText(text);
				( (TextView) findViewById(R.id.ReceivedAt) ).setText(timestampText);
			}

		}
	}
}