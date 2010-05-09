package bankdroid.start;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import bankdroid.start.ServiceRunner.ServiceListener;

import com.csaba.connector.BankService;
import com.csaba.connector.ServiceException;

public abstract class ServiceActivity extends Activity implements Codes, ServiceListener
{

	public static final int MESSAGE_DIALOG = 123;
	private String dialogMessage = null;

	public ServiceActivity()
	{
		super();
	}

	@Override
	public void onServiceFailed( final BankService service, final Throwable tr )
	{
		setDialogMessage(tr instanceof ServiceException ? ( (ServiceException) tr ).getNativeMessage() : tr
				.getMessage());
		showDialog(MESSAGE_DIALOG); //FIXME finish this
	}

	@Override
	protected Dialog onCreateDialog( final int id )
	{
		Dialog dialog = null;
		switch ( id )
		{
		case MESSAGE_DIALOG:
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getDialogMessage()).setCancelable(false).setPositiveButton("Ok", null);
			dialog = builder.create();
			break;

		default:
			break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog( final int id, final Dialog dialog )
	{
		super.onPrepareDialog(id, dialog);
		if ( id == MESSAGE_DIALOG )
		{
			( (AlertDialog) dialog ).setMessage(getDialogMessage());
		}
	}

	public void setDialogMessage( final String dialogMessage )
	{
		this.dialogMessage = dialogMessage;
	}

	public String getDialogMessage()
	{
		return dialogMessage;
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
			startActivity(new Intent(getBaseContext(), Preferences.class));
		}
		return true;
	}
}