package hu.androidportal.rss;

import java.util.ArrayList;
import java.util.List;

public class RSSChannel extends RSSObject
{
	public final static String F_LANGUAGE = "language";

	public String language;

	public List<RSSItem> items = new ArrayList<RSSItem>();

	@Override
	public String toString()
	{
		return "RSSChannel: " + super.toString() + "|" + language + "|" + items.size();
	}
}
