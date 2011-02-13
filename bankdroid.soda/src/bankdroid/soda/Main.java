package bankdroid.soda;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Main extends MenuActivity implements Codes
{
	private final static int DIALOG_NOCODE = 567;

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
			showDialog(DIALOG_NOCODE);
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

	@Override
	protected Dialog onCreateDialog( final int id )
	{
		final Dialog dialog;
		switch ( id )
		{
		case DIALOG_NOCODE:
			// do the work to define the pause Dialog
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.noMessageYet).setCancelable(false).setNeutralButton(R.string.ok,
					new DialogInterface.OnClickListener()
					{
						public void onClick( final DialogInterface dialog, final int id )
						{
							dialog.cancel();
						}
					});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
}
