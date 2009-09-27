package bankdroid.start;

import com.csaba.connector.BankService;
import com.csaba.connector.model.Session;

public class ServiceRunner implements Runnable
{

	public interface ServiceListener
	{
		void onServiceFinished( BankService service );

		void onServiceFailed( BankService service, Throwable tr );
	}

	private final ServiceListener listener;
	private final BankService service;
	private final Session session;

	private ServiceRunner( final ServiceListener listener, final BankService service, final Session session )
	{
		this.listener = listener;
		this.service = service;
		this.session = session;
	}

	@Override
	public void run()
	{
		try
		{
			service.execute(session);
			listener.onServiceFinished(service);
		}
		catch ( final Exception e )
		{
			listener.onServiceFailed(service, e);
		}
	}

	public static void runService( final BankService service, final ServiceListener listener, final Session session )
	{
		new Thread(new ServiceRunner(listener, service, session)).start();
	}

}
