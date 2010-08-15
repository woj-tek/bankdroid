package bankdroid.start;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.csaba.connector.BankService;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Session;

public class ServiceRunner implements Runnable, Codes
{

	public interface ServiceListener
	{
		void onServiceFinished( BankService service );

		void onServiceFailed( BankService service, Throwable tr );
	}

	private final ServiceListener listener;
	private final BankService service;
	private final Session session;

	private ProgressDialog dialog;
	private final Context context;
	private Handler handler;

	public ServiceRunner( final Context context, final ServiceListener listener, final BankService service,
			final Session session )
	{
		this.listener = listener;
		this.service = service;
		this.session = session;
		this.context = context;
	}

	public void start()
	{
		final SessionManager sessions = SessionManager.getInstance();
		try
		{
			sessions.setActiveService(service);
		}
		catch ( final ServiceException e )
		{
			listener.onServiceFailed(service, e);
			return;
		}

		handler = new Handler()
		{
			@Override
			public void handleMessage( final Message msg )
			{
				super.handleMessage(msg);

				if ( msg.what == SERVICE_FAILED || msg.what == SERVICE_PROCESS )
				{
					if ( !( context instanceof ServiceActivity ) || !( (ServiceActivity) context ).stopProgress() )
					{
						if ( dialog != null )
						{
							dialog.dismiss();
							dialog = null;
						}
					}
					if ( msg.what == SERVICE_PROCESS )
					{
						listener.onServiceFinished(service);
					}
					else if ( msg.what == SERVICE_FAILED )
					{
						listener.onServiceFailed(service, (Throwable) msg.getData().getSerializable(SERVICE_EXCEPTION));
					}

				}
			}
		};

		new Thread(this).start();

		if ( !( context instanceof ServiceActivity ) || !( (ServiceActivity) context ).startProgress() )
		{
			dialog = ProgressDialog.show(context, "Progress", context.getText(R.string.progressText), true);
		}
	}

	@Override
	public void run()
	{
		Message message = null;
		try
		{
			service.checkPermission(session);
			service.execute(session);
			Log.d(TAG, "Service processed succesfully: " + service.getClass().getName());

			message = Message.obtain(handler, SERVICE_PROCESS);
		}
		catch ( final Exception e )
		{
			Log.d(TAG, "Service request failed.", e);
			if ( e instanceof ServiceException )
				Log.d(TAG, "Exception: " + ( (ServiceException) e ).getNativeMessage());

			message = Message.obtain(handler, SERVICE_FAILED);
			message.getData().putSerializable(SERVICE_EXCEPTION, e);
		}
		finally
		{
			SessionManager.getInstance().clearActiveService();
		}

		handler.sendMessage(message);
	}

}
