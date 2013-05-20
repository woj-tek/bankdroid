package bankdroid.rss;

public class AtIDExtractor implements IDExtractor
{

	@Override
	public long getId( final CharSequence guid )
	{
		int i = 0;
		final int len = guid.length();
		for ( ; i < len; i++ )
			if ( !Character.isDigit(guid.charAt(i)) )
				break;

		return Long.parseLong(guid.subSequence(0, i).toString());
	}

}
