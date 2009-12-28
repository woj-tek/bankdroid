package hu.androidportal.rss;

import java.util.Date;

import android.net.Uri;

public class RSSItem extends RSSObject
{
	private static final String BREAK = "&lt;!--break-->";
	public final static String F_PUBDATE = "pubdate";
	public final static String F_AUTHOR = "author";
	public final static int MAX_SUMMARY_LENGTH = 400;

	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of banks.
	 */
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/hu.androidportal.RSSItem";

	/**
	 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single bank.
	 */
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/hu.androidportal.RSSItem";
	/**
	 * The content:// style URL for this table.
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://hu.androidportal.RSSItem/rssitems");
	public static final String DEFAULT_SORT_ORDER = F_PUBDATE + " ASC";

	public Date publishDate;
	public String author;

	@Override
	public String toString()
	{
		return "RSSItem: " + super.toString() + "|" + author + "|" + publishDate;
	}

	public void generateSummary()
	{
		//remove mistic break text from the article.
		//it should be removed from the original description
		int breakIndex = description.indexOf(BREAK);
		if ( breakIndex > 0 )
		{
			final StringBuilder builder = new StringBuilder(description);
			while ( breakIndex >= 0 )
			{
				builder.delete(breakIndex, breakIndex + BREAK.length());
				breakIndex = builder.indexOf(BREAK);
			}
			description = builder.toString();
		}

		final StringBuilder builder = new StringBuilder(description);

		//remove HTML tags
		int start = builder.indexOf("<");
		int stop = builder.indexOf(">", start);
		while ( start >= 0 && stop >= 0 && start < MAX_SUMMARY_LENGTH )
		{
			builder.delete(start, stop + 1);

			start = builder.indexOf("<");
			stop = builder.indexOf(">", start);
		}

		//normalize whitespaces
		int i = 0;
		start = -1;
		while ( i < builder.length() && !( start == -1 && i > MAX_SUMMARY_LENGTH ) )
		{
			if ( start >= 0 )
			{
				if ( !Character.isWhitespace(builder.charAt(i)) )
				{
					builder.delete(start, i); //remove all white spaces
					if ( start > 0 )
						builder.insert(start, ' '); // insert a space instead
					i = start;
					start = -1;
				}
			}
			else if ( Character.isWhitespace(builder.charAt(i)) )
			{
				start = i;
			}

			i++;
		}
		if ( start >= 0 )
		{ // this happens only when i runs out of the length
			builder.delete(start, i); //remove all white spaces at the end of the text
		}

		//trim to maximum length
		if ( builder.length() > MAX_SUMMARY_LENGTH )
			builder.delete(MAX_SUMMARY_LENGTH, builder.length());

		summary = HTMLEntities.fromHtmlEntities(builder).toString();
	}
}
