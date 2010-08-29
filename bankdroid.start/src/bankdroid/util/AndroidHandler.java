package bankdroid.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import android.util.Log;

public class AndroidHandler extends Handler
{
	private final String tag;

	public AndroidHandler( final String tag )
	{
		super();
		this.tag = tag;
	}

	@Override
	public void close()
	{
		//do nothing
	}

	@Override
	public void flush()
	{
		//do nothing
	}

	@Override
	public void publish( final LogRecord record )
	{
		if ( !( isLoggable(record) ) )
			return;
		String str;
		try
		{
			str = getFormatter().format(record);
		}
		catch ( final Exception localException1 )
		{
			reportError(null, localException1, 5);
			return;
		}

		//maybe some header should be printed here
		//this.writer.write(getFormatter().getHead(this));

		final String name = record.getLevel().getName();
		if ( name.equals(Level.FINE) || name.equals(Level.FINER) )
		{
			Log.d(tag, str);
		}
		if ( name.equals(Level.INFO) )
		{
			Log.i(tag, str);
		}
		if ( name.equals(Level.WARNING) )
		{
			Log.w(tag, str);
		}
		if ( name.equals(Level.SEVERE) )
		{
			Log.e(tag, str);
		}
		else
		{
			Log.v(tag, str);
		}
	}
}
