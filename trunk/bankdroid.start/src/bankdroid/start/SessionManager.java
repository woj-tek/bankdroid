package bankdroid.start;

import com.csaba.connector.model.Session;

public class SessionManager
{

	private SessionManager()
	{
		// singleton, do not initialize
	}

	private static SessionManager manager;

	public static SessionManager getInstance()
	{
		if ( manager == null )
			manager = new SessionManager();
		return manager;
	}

	///////METHODS

	private Session session;

	public void setSession( final Session session )
	{
		this.session = session;
	}

	public Session getSession()
	{
		return session;
	}
}
