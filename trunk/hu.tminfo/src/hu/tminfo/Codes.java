package hu.tminfo;

public interface Codes
{
	public static final String TAG = "bd.rss";

	public static final String RSSITEM_PROVIDER_AUTHORITY = "bankdroid.rss.RSSItem";

	public static final String PREF_FREQUENCY = "bankdroid.rss.Frequency";
	public static final String PREF_FEEDGROUP = "bankdroid.rss.FeedGroup";
	public static final String PREF_FEED_PREFIX = "bankdroid.rss.Feed";
	public static final String PREF_EXPIRY = "bankdroid.rss.Expiry";
	public static final String PREF_NOTIFICATION = "bankdroid.rss.Notification";
	public static final String PREF_USE_TOOLBAR = "bankdroid.rss.UseToolbar";
	public static final String PREF_DEBUG = "bankdroid.rss.Debug";
	public static final String PREF_CLEANDB = "bankdroid.rss.CleanDB";

	/**
	 * Stores the state of the toolbar for various activities derived from ToolbarActivity.
	 */
	public static final String PREF_TOOLBAR_STATE = "bankdroid.rss.ToolbarState.";
	/**
	 * Preferences item that stores the date for the next schedule. Item stores a long that 
	 * contains the next refresh time in milliseconds (similar to System.currentTimeMillis()).
	 */
	public static final String PREF_NEXT_SCHEDULE = "bankdroid.rss.NextSchedule";

	/**
	 * Preference item that stores the date of the last succesful synchronization time. It stores a 
	 * long that contains the time in millisecs (similar to System.currentTimeMillis()).
	 */
	public static final String PREF_LAST_SUCCESFUL_SYCNH = "bankdroid.rss.LastSuccesfulSynch";

	public static final boolean DEFAULT_USE_TOOLBAR = true;
	public static final String DEFAULT_FREQUENCY = "60";
	public static final String DEFAULT_EXPIRY = "30";
	public static final boolean DEFAULT_NOTIFICATION = true;
	public static final boolean DEFAULT_DEBUG = false;

	public static final String ACTION_NORMAL_START = "bankdroid.rss.action.NORMAL_START";
	public static final String ACTION_FEED_CHANGED = "bankdroid.rss.action.FEED_CHANGED";
	public static final String ACTION_MANUAL_START = "bankdroid.rss.action.MANUAL_START";
	public static final String ACTION_STOP = "bankdroid.rss.action.STOP";
	public static final String ACTION_SYNCH_NOW = "bankdroid.rss.action.SYNCH_NOW";

	public static final String EXTRA_FEEDS_REMOVED = "bankdroid.rss.extra.FeedsRemoved";
	public static final String EXTRA_FEEDS_ADDED = "bankdroid.rss.extra.FeedsAdded";

	public static final int NOTIFICATION_SERVICE_LIFECYCLE = 11;
	public static final int NOTIFICATION_ERRORS = 12;
	public static final int NOTIFICATION_THREAD_LIFECYCLE = 13;
	public static final int NOTIFICATION_WAKELOCK = 14;
	public static final int NOTIFICATION_REFRESH = 2;
	public static final int NOTIFICATION_NEWITEM = 3;

	public static final String CPU_WAKE_LOCK = "bankdroid.rss.WakeLock";

}