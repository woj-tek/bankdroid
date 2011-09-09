package bankdroid.start.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import bankdroid.start.R;
import bankdroid.start.ServiceActivity;
import bankdroid.start.plugin.PluginManager;
import bankdroid.util.GUIUtil;

import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.model.Bank;

public class AuthBankSelectActivity extends ServiceActivity implements OnItemClickListener, OnClickListener
{
	private static final int REQUEST_NEXT = 101;

	private boolean showDummyBank;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setResult(RESULT_CANCELED);

		setSessionOriented(false);
		setShowHomeMenu(false);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.authbankselect);

		final ListView bankList = (ListView) findViewById(R.id.bankList);
		try
		{
			final Bank[] banks = BankServiceFactory.getAvailableBanks();
			for ( final Bank bank : banks )
			{
				Log.d(TAG, "Bank found: " + bank.getId());
			}
			bankList.setAdapter(new BankAdapter(banks));
		}
		catch ( final Exception e )
		{
			Log.e(TAG, "Failed to initialize bank list.", e);
		}
		bankList.setOnItemClickListener(this);
		registerForContextMenu(bankList);

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

	@Override
	public void onCreateContextMenu( final ContextMenu menu, final View v, final ContextMenuInfo menuInfo )
	{
		if ( v.getId() == R.id.bankList )
		{
			final MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.banklistcontextmenu, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.toBrowser )
		{
			final long id = ( (AdapterContextMenuInfo) item.getMenuInfo() ).id;

			final BankAdapter adapter = (BankAdapter) ( (ListView) findViewById(R.id.bankList) ).getAdapter();
			final Bank bank = adapter.getBank((int) id);

			final Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(bank.getMobileBankURL()));
			startActivity(intent);
		}
		else if ( item.getItemId() == R.id.callBank )
		{
			final long id = ( (AdapterContextMenuInfo) item.getMenuInfo() ).id;
			final BankAdapter adapter = (BankAdapter) ( (ListView) findViewById(R.id.bankList) ).getAdapter();
			final Bank bank = adapter.getBank((int) id);

			final Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + bank.getCallCenterURL()));
			startActivity(intent);
		}
		return super.onContextItemSelected(item);
	}

}
