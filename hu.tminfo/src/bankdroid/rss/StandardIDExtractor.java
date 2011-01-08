package bankdroid.rss;

public class StandardIDExtractor implements IDExtractor
{
	protected long getId( final CharSequence guid, final int startIndex )
	{
		int i = startIndex;
		final int len = guid.length();
		for ( ; i < len; i++ )
			if ( guid.charAt(i) == ' ' )
				break;

		return Long.parseLong(guid.subSequence(startIndex, i).toString());
	}

	@Override
	public long getId( final CharSequence guid )
	{
		return getId(guid, 0);
	}

}
