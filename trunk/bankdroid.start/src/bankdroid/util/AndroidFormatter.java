package bankdroid.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author gyenes
 */
public class AndroidFormatter extends Formatter
{
	private boolean showLevel = true;

	public AndroidFormatter( final boolean showLevel )
	{
		this.showLevel = showLevel;
	}

	@Override
	public String format( final LogRecord record )
	{
		// seq level date time message
		final StringBuilder sb = new StringBuilder();
		if ( showLevel )
		{
			padding(sb, record.getLevel().toString(), 8, true).append(" ");
		}

		sb.append(record.getMessage());

		final Throwable tr = record.getThrown();
		if ( tr != null )
		{
			try
			{
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				tr.printStackTrace(new PrintStream(baos));
				baos.flush();
				baos.close();

				final String stackTrace = new String(baos.toByteArray());
				sb.append("\n").append(stackTrace);
			}
			catch ( final IOException e )
			{
				sb.append("\n ERROR: failed to generate stack trace for exception: " + e);
			}
		}

		return sb.append("\n").toString();
	}
	private static final String PADDING_HELPER = "                                                                      ";

	private StringBuilder padding( final StringBuilder buffer, final String what, final int size, final boolean left )
	{
		if ( left )
		{
			buffer.append(what);
			if ( what.length() < size )
			{
				buffer.append(PADDING_HELPER, 0, size - what.length());
			}
		}
		else
		{
			if ( what.length() < size )
			{
				buffer.append(PADDING_HELPER, 0, size - what.length());
			}
			buffer.append(what);
		}
		return buffer;
	}

}
