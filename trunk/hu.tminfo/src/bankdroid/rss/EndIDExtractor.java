/**
 * 
 */
package bankdroid.rss;

final class EndIDExtractor implements IDExtractor
{
	/**
	 * This is required due to avoid overlap on comments and standard item IDs. This should large enough
	 * to avoid id duplication.
	 */
	private final static long END_ID_SHIFT = 200000000L;

	//http://androidhungary.com/?p=2300
	@Override
	public long getId( final CharSequence guid )
	{
		final int len = guid.length();
		for ( int i = len - 1; i > 0; i-- )
		{
			if ( guid.charAt(i) == '=' )
				return END_ID_SHIFT + new Integer(guid.subSequence(i + 1, len).toString());
		}
		throw new IllegalArgumentException("Invalid guid value for EndIDExctractor: " + guid);
	}
}