package bankdroid.rss;

import java.text.MessageFormat;

/**
 * @author Gabe
 * 
 * Replace youtube blocks to mobile friendly version:
 *
 */
public class YoutubeFilter implements ContentFilter
{
	private static final String OBJECT_START = "<object ";
	private static final String OBJECT_FINISH = "</object>";
	private static final String YOUTUBE_URL = "www.youtube.com/v/";

	private static final String TEMPLATE = "<a href=\"http://www.youtube.com/watch?v={0}\">"
			+ "<img src=\"file:///android_asset/youtube_layer.png\" style=\"width:320px; height:240px; margin:0; padding:0; background-size: cover; background:transparent url(http://img.youtube.com/vi/{0}/default.jpg) no-repeat center\"/>"
			+ "</a>";

	@Override
	public StringBuilder filter( final StringBuilder content )
	{
		int start = content.indexOf(OBJECT_START);

		while ( start >= 0 )
		{
			int youtubeLink = content.indexOf(YOUTUBE_URL, start + 1);
			final int stop = content.indexOf(OBJECT_FINISH, start + 1);
			if ( youtubeLink > 0 && stop > 0 && stop > youtubeLink )
			{
				//valid block found
				String videoId = null;
				youtubeLink += YOUTUBE_URL.length();
				int linkEnd = youtubeLink;
				char c = content.charAt(linkEnd);
				while ( ( c >= 'A' && c <= 'Z' ) || ( c >= 'a' && c <= 'z' ) || ( c >= '0' && c <= '9' ) || c == '-'
						|| c == '_' )
				{
					linkEnd++;
					c = content.charAt(linkEnd);
				}
				videoId = content.substring(youtubeLink, linkEnd);

				content.delete(start, stop);
				final String template = MessageFormat.format(TEMPLATE, videoId);
				content.insert(start, template);
			}

			start = content.indexOf(OBJECT_START, stop);
		}
		return content;
	}
}
