package bankdroid.rss;

/**
 * @author Gabe
 * This interface can be used to extract numeric id from the guid element of the RSS feed.
 */
public interface IDExtractor
{
	long getId( CharSequence guid );
}
