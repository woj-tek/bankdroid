package bankdroid.rss;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import android.net.Uri;

/**
 * @author Gabe
 */
public class RSSItem extends RSSObject
{
	public static final char CHANNEL_SEPARATOR = '|';
	private static final String BREAK = "&lt;!--";
	private static final String BREAK_REPLACE = "<!--";

	public final static String F_PUBDATE = "pubdate";
	public final static String F_AUTHOR = "author";
	public final static String F_CHANNELS = "channels";
	public final static String F_STATUS = "status";

	public static final int STATUS_UNREAD = 0;
	public static final int STATUS_READ = 1;

	public final static int MAX_SUMMARY_LENGTH = 400;

	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of banks.
	 */
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/bankdroid.rss.RSSItem";

	/**
	 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single bank.
	 */
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/bankdroid.rss.RSSItem";
	/**
	 * The content:// style URL for this table.
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://bankdroid.rss.RSSItem/rssitems");
	public static final String DEFAULT_SORT_ORDER = F_PUBDATE + " ASC";

	public Date publishDate;
	public String author;
	public int status = STATUS_UNREAD;
	public Set<String> channels = new HashSet<String>();

	private static final ContentFilter youtubeFilter = new YoutubeFilter();

	@Override
	public String toString()
	{
		return "RSSItem: " + super.toString() + "|" + author + "|" + publishDate;
	}

	public void generateSummary()
	{
		//postprocess items here

		//append few line breaks to save space for the toolbar.
		final StringBuilder descBuilder = new StringBuilder(description);
		descBuilder.append("<br/><br/><br/><br/>");

		//remove mistic break text from the article.
		//it should be removed from the original description
		int breakIndex = descBuilder.indexOf(BREAK);
		if ( breakIndex >= 0 )
		{
			while ( breakIndex >= 0 )
			{
				descBuilder.delete(breakIndex, breakIndex + BREAK.length());
				descBuilder.insert(breakIndex, BREAK_REPLACE);
				breakIndex = descBuilder.indexOf(BREAK, breakIndex);
			}
		}

		//remove "Tweet" here
		final int tweetIndex = descBuilder.indexOf("<div class=\"tweetbutton\"");
		if ( tweetIndex >= 0 )
		{
			descBuilder.delete(tweetIndex, descBuilder.indexOf("</div>", tweetIndex) + 6);
		}

		//fix youtube links
		youtubeFilter.filter(descBuilder);

		description = descBuilder.toString();

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

		summary = HTMLEntities.fromHtmlEntities(builder.toString());
	}

	public String getChannelsAsString()
	{
		final StringBuilder chString = new StringBuilder();
		for ( final String channel : channels )
		{
			chString.append(CHANNEL_SEPARATOR);
			chString.append(channel);
		}
		chString.append(CHANNEL_SEPARATOR);
		return chString.toString();
	}

	public void setChannelsAsString( final String chString )
	{
		final StringTokenizer tokens = new StringTokenizer(chString, "" + CHANNEL_SEPARATOR);

		channels.clear();
		while ( tokens.hasMoreTokens() )
		{
			channels.add(tokens.nextToken());
		}
	}

	public static String mergeChannels( final String currentChannels, final String newChannels )

	{
		final StringBuilder result = new StringBuilder(currentChannels);
		final StringTokenizer news = new StringTokenizer(newChannels);
		while ( news.hasMoreTokens() )
		{
			final String channel = news.nextToken();
			if ( result.indexOf(channel) < 0 )
				result.append(channel).append(CHANNEL_SEPARATOR);
		}
		return result.toString();
	}

	public static boolean isChannelContained( final String currentChannels, final String newChannels )
	{
		final StringTokenizer news = new StringTokenizer(newChannels);
		while ( news.hasMoreTokens() )
		{
			final String channel = news.nextToken();
			if ( !currentChannels.contains(channel) )
			{
				return false;
			}
		}
		return true;
	}

	public static String removeChannelFromList( final String list, final String tag )
	{
		final int index = list.indexOf(tag);
		if ( index >= 0 )
		{
			final StringBuilder listBuilder = new StringBuilder(list);
			listBuilder.delete(index, index + tag.length() + 1); //removing the tag and an extra SEPARATOR
			if ( listBuilder.length() < 3 )
			{
				//empty list remains
				return "";
			}
			else
			{
				return listBuilder.toString();
			}
		}
		else
		{
			return list;
		}
	}

	/**
	 * convert channel string to a user friendly string.
	 * @param channelString
	 * @return
	 */
	public static String convertChannelString( final String channelString )
	{
		final StringBuilder builder = new StringBuilder(channelString);
		if ( builder.charAt(0) == CHANNEL_SEPARATOR )
		{
			builder.delete(0, 1);
		}
		if ( builder.charAt(builder.length() - 1) == CHANNEL_SEPARATOR )
		{
			builder.delete(builder.length() - 1, builder.length());
		}

		int i = 0;
		while ( ( i = builder.indexOf("" + CHANNEL_SEPARATOR) ) >= 0 )
		{
			builder.replace(i, i + 1, ", ");
		}
		return builder.toString();
	}
}
