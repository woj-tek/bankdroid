package bankdroid.soda;

import java.io.Serializable;
import java.util.Date;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
 * <li>TODO: clear SMS based on preferences (that may be problematic. There is no good tip for it on forums.</li>
 * <li>TODO: maintain how many OTP to be stored per bank - it makes no sense. Only one should be stored per bank.</li>
 * <li>TODO: display last SMS on start up</li>
 * <li>TODO: display list of banks and their settings</li>
 * <li>TODO: let the user to register new banks, store settings in DB</li>
 * <li>TODO: let the user to post the bank settings to the bankdroid@googlecode.com</li>
 * <li>TODO: displays a count-down to indicate when the OTP will expire</li>
 * </ul>
 * 
 * @author user
 *
 */
public class SMSOTPDisplay extends Activity implements View.OnClickListener, Codes
{
	private CharSequence displayedCode;
	private Bank bank;
	private Date receivedAt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		( (Button) findViewById(R.id.codeButton) ).setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		//clear notification if there is any
		final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		nm.cancel(NOTIFICATION_ID);

		//process intent
		final Intent intent = getIntent();

		Serializable timestampSource = null;
		if ( intent != null )
		{
			timestampSource = intent.getSerializableExtra(BANKDROID_SODA_SMSTIMESTAMP);
		}

		if ( timestampSource != null )
		{
			Log.d(TAG, "Set values based on new SMS intent.");
			final String smsCode = intent.getStringExtra(BANKDROID_SODA_SMSCODE);
			final Bank source = (Bank) intent.getSerializableExtra(BANKDROID_SODA_BANK);

			setValues(source, smsCode, (Date) timestampSource);
		}
		else if ( displayedCode != null )
		{
			Log.d(TAG, "Restore old values");
			setValues(bank, displayedCode.toString(), receivedAt);
		}
		else
		{
			Log.d(TAG, "Clear fields as there is no intent and no previously set values.");
			setValues(null, null, null);
		}

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
			displayedCode = savedInstanceState.getCharSequence(BANKDROID_SODA_SMSCODE);
			Log.d(TAG, "Values restored for code: " + displayedCode + "(" + bank.getName() + ")");
		}
	}

	private void setValues( final Bank source, final String code, final Date receivedAt )
	{
		displayedCode = code;
		bank = source;
		this.receivedAt = receivedAt;

		CharSequence timestampText = "";
		if ( source != null )
		{
			Log.i(TAG, "One time password to display from Bank = " + source.getName());
			timestampText = Formatters.getTimstampFormat().format(receivedAt);
		}

		( (ImageView) findViewById(R.id.BankLogo) ).setImageDrawable(source == null ? null : getResources()
				.getDrawable(source.getIconId()));
		( (Button) findViewById(R.id.codeButton) ).setText(code == null ? getResources().getText(R.string.nocode)
				: code);
		( (TextView) findViewById(R.id.ReceivedAt) ).setText(getResources().getText(R.string.received_prefix)
				.toString()
				+ timestampText);
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
		if ( v.getId() == R.id.codeButton )
		{
			if ( displayedCode != null )
			{
				( (ClipboardManager) getSystemService(CLIPBOARD_SERVICE) ).setText(displayedCode);
			}
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu( final Menu menu )
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.MenuPreferences )
		{
			Log.d(TAG, "Preferences menu selected.");
			final Intent settingsActivity = new Intent(getBaseContext(), Preferences.class);
			startActivity(settingsActivity);
		}
		else if ( item.getItemId() == R.id.MenuBanks )
		{
			Log.d(TAG, "Bank List menu selected.");
			final Intent bankListIntent = new Intent();
			bankListIntent.setClass(getBaseContext(), BankListActivity.class);
			startActivity(bankListIntent);
		}
		else if ( item.getItemId() == R.id.MenuClear )
		{
			Log.d(TAG, "Clear menu selected.");
			setValues(null, null, null);
		}
		return false;
	}
}