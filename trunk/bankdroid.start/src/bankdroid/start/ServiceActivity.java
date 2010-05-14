package bankdroid.start;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import bankdroid.start.ServiceRunner.ServiceListener;
import bankdroid.util.TrackedActivity;

import com.csaba.connector.BankService;
import com.csaba.connector.ServiceException;

public abstract class ServiceActivity extends TrackedActivity implements Codes, ServiceListener
{

	public static final int MESSAGE_DIALOG = 123;
	private String dialogMessage = null;
	private boolean sessionOriented = true;

	public ServiceActivity()
	{
		super();
	}

	@Override
	public void onServiceFailed( final BankService service, final Throwable tr )
	{
		String message = tr.getMessage();

		if ( tr instanceof ServiceException )
		{
			final String nativeMessage = ( (ServiceException) tr ).getNativeMessage();
			if ( nativeMessage != null && nativeMessage.length() > 0 )
				message = message + "\n" + nativeMessage;
		}
		setDialogMessage(message);
		showDialog(MESSAGE_DIALOG);
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
	protected void onResume()
	{
		super.onResume();

		if ( sessionOriented && SessionManager.getInstance().getSession() == null )
		{
			startActivityForResult(new Intent(getBaseContext(), StartActivity.class), LOGIN);
		}
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

	public void setSessionOriented( final boolean sessionOriented )
	{
		this.sessionOriented = sessionOriented;
	}

	public boolean isSessionOriented()
	{
		return sessionOriented;
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		super.onActivityResult(requestCode, resultCode, data);
		if ( requestCode == LOGIN && resultCode == RESULT_CANCELED )
		{
			finish();
		}
	}
}