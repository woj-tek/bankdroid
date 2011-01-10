package bankdroid.rss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Formatters
{
	private Formatters()
	{
		// to avoid instantiation
	}
	////////////// default java date format

	private static final ThreadLocal<DateFormat> defaultFactory = new ThreadLocal<DateFormat>()
	{
		@Override
		protected DateFormat initialValue()
		{
			return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
		}
	};

	public final static DateFormat getDefaultFormat()
	{
		return defaultFactory.get();
	}

	////////////// DATE

	private static final ThreadLocal<DateFormat> dateFactory = new ThreadLocal<DateFormat>()
	{
		@Override
		protected DateFormat initialValue()
		{
			return new SimpleDateFormat("yyyy.MM.dd");
		}
	};

	public final static DateFormat getShortDateFormat()
	{
		return dateFactory.get();
	}

	////////////// TIMESTAMP

	private static final ThreadLocal<DateFormat> timestampFactory = new ThreadLocal<DateFormat>()
	{
		@Override
		protected DateFormat initialValue()
		{
			return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		}
	};

	public final static DateFormat getTimstampFormat()
	{
		return timestampFactory.get();
	}

}
