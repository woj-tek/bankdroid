package bankdroid.soda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SubmitActivity extends Activity implements OnClickListener, Codes
{

	private static final String SUBMISSION_ADDRESS = "bankdroid@gmail.com"; //FIXME set correct e-mail
	private static final int PICKSMS_ACTIVITY = 8001;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.submit);

		( (Button) findViewById(R.id.start) ).setOnClickListener(this);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.start )
		{
			pickSMS();
		}
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		//FIXME create seperate activity for submitting result
		super.onActivityResult(requestCode, resultCode, data);

		if ( resultCode == RESULT_CANCELED )
		{
			Toast.makeText(getBaseContext(), "Submission of sample SMS is cancelled.", Toast.LENGTH_SHORT); //XXX string
			return;
		}

		if ( requestCode == PICKSMS_ACTIVITY )
		{
			final String address = data.getStringExtra(BANKDROID_SODA_ADDRESS);
			final String body = data.getStringExtra(BANKDROID_SODA_SMSMESSAGE);
			Log.d(TAG, "SMS was selected: " + address + " :: " + body);

			//construct e-mail body
			final StringBuilder builder = new StringBuilder();

			builder.append("Bank address: ").append(address).append("\n").append("\n");
			builder.append("SMS OTP text: ").append(body).append("\n");

			sendEmail(new String[] { SUBMISSION_ADDRESS }, "SMS OTP Sample", builder.toString());
		}
	}

	private void pickSMS()
	{
		final Intent intent = new Intent();
		intent.setClass(getBaseContext(), SMSListActivity.class);
		startActivityForResult(intent, PICKSMS_ACTIVITY);
	}

	private void sendEmail( final String[] address, final String subject, final String msg )
	{
		final Intent send = new Intent(Intent.ACTION_SEND);
		send.putExtra(Intent.EXTRA_EMAIL, address);
		send.putExtra(Intent.EXTRA_SUBJECT, subject);
		send.putExtra(Intent.EXTRA_TEXT, msg);
		//send.setType("message/rfc822");
		send.setType("text/plain");
		startActivity(Intent.createChooser(send, "Select account:"));
	}

}
