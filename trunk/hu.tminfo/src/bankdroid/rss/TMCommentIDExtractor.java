package bankdroid.rss;

/**
 * @author Gabe
 *
 */
public class TMCommentIDExtractor extends EndIDExtractor
{
	//http://tminfo.hu/?p=3259#comment-187
	private final static char SEPARATOR = '-';

	/**
	 * This is required due to avoid overlap on comments and standard item IDs. This should large enough
	 * to avoid id duplication.
	 */
	private final static long TM_COMMENT_ID_SHIFT = 300000000L;

	@Override
	public long getId( final CharSequence guid )
	{
		return getId(guid, SEPARATOR, TM_COMMENT_ID_SHIFT);
	}

}
