package hu.androidportal.rss;

public class RSSObject
{
	public final static String F__ID = "_id";
	public final static String F_TITLE = "title";
	public final static String F_DESCRIPTION = "description";
	public final static String F_SUMMARY = "summary";
	public final static String F_LINK = "link";

	public long id;

	public String title;
	public String description;
	public String link;
	public String summary;

	public RSSObject()
	{
		super();
	}

	@Override
	public String toString()
	{
		return "RSSObject:" + id + "|" + title + "|" + link + "|" + summary;
	}
}