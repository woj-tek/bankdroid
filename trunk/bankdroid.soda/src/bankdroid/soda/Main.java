package bankdroid.soda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Main extends MenuActivity implements Codes
{
	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		Eula.show(this);

		setContentView(R.layout.main);
	}

	public void onSubmitSample( final View v )
	{
		final Intent submitIntent = new Intent(getBaseContext(), SMSListActivity.class);
		startActivity(submitIntent);
	}

	public void onViewLastCode( final View v )
	{
		final Message message = BankManager.getLastMessage(getApplicationContext());
		if ( message == null )
		{
			final Toast toast = Toast.makeText(getApplicationContext(), R.string.noMessageYet, Toast.LENGTH_SHORT);
			toast.show();
		}
		else
		{
			final Intent intent = new Intent(getBaseContext(), SMSOTPDisplay.class);
			intent.setAction(ACTION_REDISPLAY);
			intent.putExtra(BANKDROID_SODA_SMSMESSAGE, message.message);
			intent.putExtra(BANKDROID_SODA_BANK, message.bank);
			intent.putExtra(BANKDROID_SODA_SMSCODE, message.bank.extractCode(message.message));
			intent.putExtra(BANKDROID_SODA_SMSTIMESTAMP, message.timestamp);

			startActivity(intent);
		}
	}

	public void onManageBank( final View v )
	{
		final Intent bankListIntent = new Intent(getBaseContext(), BankListActivity.class);
		startActivity(bankListIntent);
	}

}
