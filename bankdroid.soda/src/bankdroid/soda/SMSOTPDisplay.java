package bankdroid.soda;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import bankdroid.soda.CountDown.CountDownListener;

/**
 * This view as able to display SMS one time passwords processed by {@link SMSReceiver}. Besides displayed the codes
 * it provides several conveniences services:
 * <ul>
 * <li>Display code in large letters for the better readability</li>
 * <li>Display a copy button to copy the code into the clipboard. In this way it is easy to 
 * 		copy and paste it into the appropriate field in the Browser</li>
 * <li>create menu: clear, preferences, bank list</li>
 * <li>handle preferences</li>
 * <li>improved design</li>
 * <li>use notifications instead of direct pop-up based on user preferences</li>
 * <li>clear SMS based on preferences (that may be problematic. There is no good tip for it on forums.</li>
 * <li>display a different activity on start up, from where various actions can be started.</li>
 * <li>displays list of banks and their settings</li>
 * <li>let the user to register new banks, store settings in DB</li>
 * <li>displays a count-down to indicate when the OTP will expire</li>
 * <li>German translations</li>
 * <li>displays transaction signing security warning</li>
 * </ul>
 * 
 * @author user
 *
 */
public class SMSOTPDisplay extends MenuActivity implements Codes, CountDownListener, SensorEventListener
{
	private static final int FORCE_THRESHOLD = 900;
	private static final int MSG_DELETE_SMS = 0;

	private CharSequence displayedCode;
	private Bank bank;
	private Date receivedAt;
	private String smsMessage;
	private CountDown countDown;

	boolean isActive = false;

	private SensorManager sensorManager;
	private Sensor sensor;
	private long lastUpdate = -1;
	private float lastX, lastY, lastZ;
	private SharedPreferences settings;

	/** Called when the activity is first created. */
	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		Eula.show(this);

		setContentView(R.layout.sod);

		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final boolean unlockScreen = settings.getBoolean(PREF_UNLOCK_SCREEN, true);
		if ( unlockScreen && Build.VERSION.SDK_INT >= 5 )
		{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		}

		this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		final List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if ( sensors.size() > 0 )
		{
			sensor = sensors.get(0);
		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if ( sensor != null )
		{
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		}

		//clear notification if there is any
		final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		nm.cancel(NOTIFICATION_ID);

		if ( !processIntent() )
		{
			if ( displayedCode != null )
			{
				Log.d(TAG, "Restore old values");
				setValues(bank, displayedCode.toString(), receivedAt, smsMessage);
			}
			else
			{
				Log.d(TAG, "Clear fields as there is no intent and no previously set values.");
				setValues(null, null, null, null);
			}
		}
		isActive = true;

		if ( smsMessage != null )
		{
			final boolean keepSMS = settings.getBoolean(PREF_KEEP_SMS, true);

			if ( !keepSMS )
			{
				final Handler handler = new Handler()
				{
					@Override
					public void handleMessage( final android.os.Message msg )
					{
						super.handleMessage(msg);
						if ( msg.what == MSG_DELETE_SMS )
						{
							final ContentResolver cr = getContentResolver();
							final Uri uri = Uri.parse("content://sms");

							cr.delete(uri, "body=?", new String[] { (String) msg.obj });
						}
					}

				};

				handler.sendMessageDelayed(handler.obtainMessage(MSG_DELETE_SMS, smsMessage), 2500);
			}
		}

		final boolean keepScreenOn = settings.getBoolean(PREF_KEEP_SCREEN_ON, false);
		findViewById(R.id.codeButton).setKeepScreenOn(keepScreenOn);
	}

	private boolean processIntent()
	{
		//process intent
		final Intent intent = getIntent();

		Serializable timestampSource = null;
		if ( intent != null )
		{
			timestampSource = intent.getSerializableExtra(BANKDROID_SODA_SMSTIMESTAMP);
		}

		//check timestamp availability to make sure, that intent is not null, and correct intent is received.
		if ( timestampSource != null )
		{
			Log.d(TAG, "Set values based on new SMS intent.");
			final String smsCode = intent.getStringExtra(BANKDROID_SODA_SMSCODE);
			final String smsMessage = intent.getStringExtra(BANKDROID_SODA_SMSMESSAGE);
			final Bank source = (Bank) intent.getSerializableExtra(BANKDROID_SODA_BANK);

			setValues(source, smsCode, (Date) timestampSource, smsMessage);

			if ( intent.getAction().equals(ACTION_DISPLAY) )
				BankManager.updateLastMessage(getApplicationContext(), new Message(bank, smsMessage, receivedAt));

			return true;
		}

		return false;
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		if ( countDown != null )
		{
			countDown.forceStop();
			countDown = null;
		}
		isActive = false;
	}

	@Override
	protected void onSaveInstanceState( final Bundle outState )
	{
		super.onSaveInstanceState(outState);

		if ( displayedCode != null )
		{
			Log.d(TAG, "Values going to be saved for code: " + displayedCode + "(" + bank.getName() + ")");
			outState.putCharSequence(BANKDROID_SODA_SMSCODE, displayedCode);
			outState.putSerializable(BANKDROID_SODA_SMSTIMESTAMP, receivedAt);
			outState.putString(BANKDROID_SODA_SMSMESSAGE, smsMessage);
			outState.putSerializable(BANKDROID_SODA_BANK, bank);
		}
	}

	@Override
	protected void onRestoreInstanceState( final Bundle savedInstanceState )
	{
		super.onRestoreInstanceState(savedInstanceState);

		if ( savedInstanceState.containsKey(BANKDROID_SODA_SMSCODE) )
		{
			bank = (Bank) savedInstanceState.getSerializable(BANKDROID_SODA_BANK);
			receivedAt = (Date) savedInstanceState.getSerializable(BANKDROID_SODA_SMSTIMESTAMP);
			smsMessage = savedInstanceState.getString(BANKDROID_SODA_SMSMESSAGE);
			displayedCode = savedInstanceState.getCharSequence(BANKDROID_SODA_SMSCODE);
			Log.d(TAG, "Values restored for code: " + displayedCode + "(" + bank.getName() + ")");
		}
	}

	private void setValues( final Bank source, final String code, final Date receivedAt, final String smsMessage )
	{
		displayedCode = code;
		bank = source;
		this.receivedAt = receivedAt;
		this.smsMessage = smsMessage;

		CharSequence timestampText = "";
		if ( source != null )
		{
			Log.i(TAG, "One time password to display from Bank = " + source.getName());
			timestampText = Formatters.getTimstampFormat().format(receivedAt);
		}

		( (ImageView) findViewById(R.id.bankLogo) )
				.setImageDrawable(source == null || source.getIconId() <= 0 ? getResources().getDrawable(
						R.drawable.bankdroid_logo) : getResources().getDrawable(source.getIconId()));
		( (TextView) findViewById(R.id.codeButton) ).setText(code == null ? getResources().getText(R.string.nocode)
				: code);
		( (TextView) findViewById(R.id.receivedAt) ).setText(getResources().getText(R.string.received_prefix)
				.toString()
				+ " " + timestampText);
		( (TextView) findViewById(R.id.messageBody) ).setText(smsMessage);

		final TextView countDownView = (TextView) findViewById(R.id.countDown);

		if ( source != null && smsMessage != null )
		{
			findViewById(R.id.securityWarning).setVisibility(
					source.isTransactionSign(smsMessage) ? View.VISIBLE : View.GONE);
		}

		if ( source != null && source.getExpiry() > 0 )
		{
			countDownView.setVisibility(View.VISIBLE);

			//calculate correct validity period from receivedAt and expiry
			final long ellapsedTime = ( Calendar.getInstance().getTimeInMillis() - receivedAt.getTime() ) / 1000; //convert to seconds
			final int remainingTime = (int) Math.max(0, source.getExpiry() - ellapsedTime);
			countDownView.setText(getResources().getText(R.string.countdown_prefix).toString() + " "
					+ convertTime(remainingTime));

			if ( remainingTime > 0 )
			{
				countDown = new CountDown(this, remainingTime);
				countDown.start();
			}
		}
		else
		{
			countDownView.setVisibility(View.GONE);
		}
	}

	private CharSequence convertTime( final int expiry )
	{
		final int hours = expiry / 3600;
		final int minutes = ( expiry % 3600 ) / 60;
		final int secs = expiry % 60;

		final StringBuilder builder = new StringBuilder();
		appendDigits(builder, hours).append(":");
		appendDigits(builder, minutes).append(":");
		appendDigits(builder, secs);

		return builder.toString();
	}

	private StringBuilder appendDigits( final StringBuilder builder, final int digits )
	{
		if ( digits < 10 )
			builder.append('0');
		if ( digits == 0 )
			builder.append('0');
		else
			builder.append(String.valueOf(digits));
		return builder;
	}

	@Override
	protected void onNewIntent( final Intent intent )
	{
		super.onNewIntent(intent);
		setIntent(intent);

		Serializable timestampSource = null;
		if ( intent != null )
		{
			timestampSource = intent.getSerializableExtra(BANKDROID_SODA_SMSTIMESTAMP);
		}
		Log.d(TAG, "Intent timestamp: " + timestampSource);

		if ( isActive )
			processIntent();
	}

	public void onCopyAndClose( final View v )
	{
		if ( displayedCode != null )
		{
			( (ClipboardManager) getSystemService(CLIPBOARD_SERVICE) ).setText(displayedCode);
		}

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu( final Menu menu )
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sodmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.menuBanks )
		{
			startActivity(new Intent(this, BankListActivity.class));
			return true;
		}
		if ( item.getItemId() == R.id.menuClear )
		{
			Log.d(TAG, "Clear menu selected.");
			setValues(null, null, null, null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void stop()
	{
		// do nothing
		sensorManager.unregisterListener(this);

	}

	@Override
	public void tick( final int remainingSec )
	{
		final TextView countDown = (TextView) findViewById(R.id.countDown);
		countDown.setText(getResources().getText(R.string.countdown_prefix).toString() + " "
				+ convertTime(remainingSec));
	}

	@Override
	public void onAccuracyChanged( final Sensor s, final int valu )
	{
		//do nothing
	}

	@Override
	public void onSensorChanged( final SensorEvent event )
	{

		if ( event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || event.values.length < 3 )
			return;

		final long currentTime = System.currentTimeMillis();

		if ( ( currentTime - lastUpdate ) > 100 )
		{
			final long diffTime = ( currentTime - lastUpdate );
			lastUpdate = currentTime;

			final float x = event.values[SensorManager.DATA_X];
			final float y = event.values[SensorManager.DATA_Y];
			final float z = event.values[SensorManager.DATA_Z];

			final float currentForce = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

			if ( currentForce > FORCE_THRESHOLD )
			{
				//device has been shaken 
				onCopyAndClose(null);
			}

			lastX = x;
			lastY = y;
			lastZ = z;
		}
	}

}