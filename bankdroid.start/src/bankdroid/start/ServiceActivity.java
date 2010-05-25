package bankdroid.start;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import bankdroid.start.ServiceRunner.ServiceListener;
import bankdroid.util.TrackedActivity;

import com.csaba.connector.BankService;
import com.csaba.connector.ServiceException;
import com.csaba.connector.service.LogoutService;

public abstract class ServiceActivity extends TrackedActivity implements Codes, ServiceListener
{

	public static final int MESSAGE_DIALOG = 123;
	private String dialogMessage = null;
	private boolean sessionOriented = true;
	private boolean showHomeMenu = true;

	public boolean isShowHomeMenu()
	{
		return showHomeMenu;
	}

	public void setShowHomeMenu( final boolean showHomeMenu )
	{
		this.showHomeMenu = showHomeMenu;
	}

	public ServiceActivity()
	{
		super();
	}

	public boolean startProgress()
	{
		final ProgressBar progress = ( (ProgressBar) findViewById(R.id.progressIndicator) );
		if ( progress != null )
		{
			progress.setVisibility(View.VISIBLE);
		}

		return progress != null;
	}

	public boolean stopProgress()
	{
		final ProgressBar progress = ( (ProgressBar) findViewById(R.id.progressIndicator) );
		if ( progress != null )
		{
			progress.setVisibility(View.INVISIBLE);
		}

		return progress != null;
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
	public void onServiceFinished( final BankService service )
	{
		if ( service instanceof LogoutService )
		{
			finish();
		}
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

		if ( sessionOriented && !SessionManager.getInstance().isLoggedIn() )
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

		menu.findItem(R.id.menuHome).setEnabled(showHomeMenu);
		menu.findItem(R.id.menuLogout).setEnabled(SessionManager.getInstance().isLoggedIn());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		if ( item.getItemId() == R.id.menuPreferences )
		{
			startActivity(new Intent(getBaseContext(), Preferences.class));
		}
		else if ( item.getItemId() == R.id.menuHome )
		{
			startActivity(new Intent(getBaseContext(), MainActivity.class));
		}
		else if ( item.getItemId() == R.id.menuLogout )
		{
			SessionManager.getInstance().logout(this);
		}
		else if ( item.getItemId() == R.id.menuAbout )
		{
			startActivity(new Intent(getBaseContext(), AboutActivity.class));
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