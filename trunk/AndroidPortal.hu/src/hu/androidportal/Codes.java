package hu.androidportal;

public interface Codes
{
	public static final String URL_ANDROIDPORTAL_HU = "http://androidportal.hu";

	public static final String TAG = "ap.hu";

	public static final String RSSITEM_PROVIDER_AUTHORITY = "hu.androidportal.RSSItem";

	public static final String PREF_FREQUENCY = "hu.androidportal.Frequency";
	public static final String PREF_FEED = "hu.androidportal.Feed";
	public static final String PREF_NOTIFICATION = "hu.androidportal.Notification";
	public static final String PREF_DEBUG = "hu.androidportal.Debug";

	/**
	 * Preferences item that stores the date for the next schedule. Item stores a long that 
	 * contains the next refresh time in milliseconds (similar to System.currentTimeMillis()).
	 */
	public static final String PREF_NEXT_SCHEDULE = "hu.androidportal.NextSchedule";

	/**
	 * Preference item that stores the date of the last succesful synchronization time. It stores a 
	 * long that contains the time in millisecs (similar to System.currentTimeMillis()).
	 */
	public static final String PREF_LAST_SUCCESFUL_SYCNH = "hu.androidportal.LastSuccesfulSynch";

	public static final String DEFAULT_FREQUENCY = "60";
	public static final String DEFAULT_FEED = "http://feeds.feedburner.com/magyarandroidportal";
	public static final boolean DEFAULT_NOTIFICATION = true;
	public static final boolean DEFAULT_DEBUG = false;

	public static final String ACTION_NORMAL_START = "hu.androidportal.action.NORMAL_START";
	public static final String ACTION_FEED_CHANGED = "hu.androidportal.action.FEED_CHANGED";
	public static final String ACTION_MANUAL_START = "hu.androidportal.action.MANUAL_START";
	public static final String ACTION_STOP = "hu.androidportal.action.STOP";
	public static final String ACTION_SYNCH_NOW = "hu.androidportal.action.SYNCH_NOW";

	public static final int NOTIFICATION_SERVICE_LIFECYCLE = 11;
	public static final int NOTIFICATION_ERRORS = 12;
	public static final int NOTIFICATION_THREAD_LIFECYCLE = 13;
	public static final int NOTIFICATION_REFRESH = 2;
	public static final int NOTIFICATION_NEWITEM = 3;

	/**
	 * Number of days that the items should be stored.
	 */
	public static final int MAX_ITEMS_TO_STORE = 10;//XXX add to preferences

	public static final String CPU_WAKE_LOCK = "hu.androidportal.WakeLock";

}