package hu.androidportal;

public interface Codes
{
	public static final String URL_ANDROIDPORTAL_HU = "http://androidportal.hu";

	public static final String TAG = "ap.hu";

	public static final String RSSITEM_PROVIDER_AUTHORITY = "hu.androidportal.RSSItem";

	public static final String PREF_FREQUENCY = "hu.androidportal.Frequency";
	public static final String PREF_FEED = "hu.androidportal.Feed";
	public static final String PREF_NOTIFICATION = "hu.androidportal.Notification";

	public static final String DEFAULT_FREQUENCY = "60";
	public static final String DEFAULT_FEED = "http://feeds.feedburner.com/magyarandroidportal";
	public static final boolean DEFAULT_NOTIFICATION = true;

	public static final String ACTION_NORMAL_START = "hu.androidportal.action.NORMAL_START";
	public static final String ACTION_FREQ_CHANGED = "hu.androidportal.action.FREQ_CHANGED";
	public static final String ACTION_FEED_CHANGED = "hu.androidportal.action.FEED_CHANGED";
	public static final String ACTION_MANUAL_START = "hu.androidportal.action.MANUAL_START";
}