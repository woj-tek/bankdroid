package bankdroid.start.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.plugin.PluginManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.model.Bank;

public class AuthBankSelectActivity extends ServiceActivity implements OnItemClickListener, OnClickListener
{
	private static final int REQUEST_NEXT = 101;

	private Bank[] banks;
	private boolean showDummyBank;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		try
		{
			banks = BankServiceFactory.getAvailableBanks();
			for ( final Bank bank : banks )
			{
				Log.d(TAG, "Bank found: " + bank.getId());
			}
		}
		catch ( final Exception e )
		{
			Log.e(TAG, "Failed to initialize bank list.", e);
		}

		setSessionOriented(false);
		setShowHomeMenu(false);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.authbankselect);

		final ListView bankList = (ListView) findViewById(R.id.bankList);

		bankList.setAdapter(new BankAdapter(banks));
		bankList.setOnItemClickListener(this);

		findViewById(R.id.rememberUser).setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Log.d(TAG, "AuthBank resume");

		setResult(RESULT_CANCELED);

		//init bank select
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		showDummyBank = preferences.getBoolean(PREF_SHOW_DUMMY_BANK, false);
		final BankAdapter adapter = (BankAdapter) ( (ListView) findViewById(R.id.bankList) ).getAdapter();
		adapter.setDummyAvailable(showDummyBank);

		final boolean rememberUsers = preferences.getBoolean(PREF_SAVE_LAST_LOGIN, false);
		( (CheckBox) findViewById(R.id.rememberUser) ).setChecked(rememberUsers);

	}

	@Override
	public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
	{
		if ( parent.getId() == R.id.bankList )
		{
			final BankAdapter adapter = (BankAdapter) parent.getAdapter();
			final Bank bank = (Bank) adapter.getItem(position);
			try
			{
				final Class<?> auhtActivity = PluginManager.getAuthActivityClass(bank);
				startActivityForResult(new Intent(this, auhtActivity), REQUEST_NEXT);
			}
			catch ( final ClassNotFoundException e )
			{
				GUIUtil.fatalError(this, e);
			}
		}
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( requestCode == REQUEST_NEXT && resultCode == RESULT_OK )
		{
			Log.d(TAG, "AuthBank finish it well.");

			//login was succesful
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onClick( final View view )
	{
		if ( view.getId() == R.id.rememberUser )
		{
			final boolean rememberUser = ( (CheckBox) view ).isChecked();
			final SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			final Editor editor = preferences.edit();
			editor.putBoolean(PREF_SAVE_LAST_LOGIN, rememberUser);
			editor.commit();
		}

	}
}
