package bankdroid.rss;

/**
 * @author Gabe
 *
 */
public class CommentIDExtractor extends StandardIDExtractor
{
	private final static String PREFIX = "comment ";
	private final static int PREFIX_LENGTH = PREFIX.length();

	/**
	 * This is required due to avoid overlap on comments and standard item IDs. This should large enough
	 * to avoid id duplication.
	 */
	private final static long COMMENT_ID_SHIFT = 100000000L;

	@Override
	public long getId( final CharSequence guid )
	{
		return getId(guid, PREFIX_LENGTH) + COMMENT_ID_SHIFT;
	}

}
