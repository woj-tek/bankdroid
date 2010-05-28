package bankdroid.start;

public interface Codes
{
	final static String TAG = "Start";

	public static final String TWITTER_URL = "http://twitter.com/bankdroid";
	public static final String GMAIL_URL = "mailto:info@bankdroid.info";
	public static final String FACEBOOK_URL = "http://www.facebook.com/pages/BankDroid/124814474207047?ref=ts";
	public static final String URL_PROJECT_HOME = "http://bit.ly/4Ys9pQ";

	final static int SERVICE_PROCESS = 1;
	final static int SERVICE_FAILED = 2;

	final static String SERVICE_EXCEPTION = "SERVICE_EXCEPTION";

	//PREFERENCES
	final static String PREF_LAST_BANK = "bankdroid.start.LastBank";
	final static String PREF_LAST_LOGINID = "bankdroid.start.LastLoginId";
	final static String PREF_LAST_PASSWORD = "bankdroid.start.LastPassword";
	final static String PREF_SAVE_LAST_LOGIN = "bankdroid.start.SaveLastLogin";
	final static String PREF_SAVE_PASSWORD = "bankdroid.start.SavePassword";
	final static String PREF_SHOW_DUMMY_BANK = "bankdroid.start.ShowDummyBank";

	//DEFAULTS
	final static String DEFAULT_LOGINID = "";
	final static String DEFAULT_PASSWORD = "";

	//OTHERS
	final static int LOGIN = 0xbaba;
	final static String DUMMY_BANK_ID = "DUMMY";
	final static int NOTIFICATION_ACTIVE_SESSION = 122112;
	final static int NOTIFICATION_SESSION_TIMEOUT = 122113;
	final static int NOTIFICATION_SESSION_TIMEOUT_EXPIRED = 122114;

	//EXTRAS
	final static String EXTRA_TRANSACTION_FILTER = "com.bankdroid.TransactionFilter";
	final static String EXTRA_PROPERTY_OBJECT = "com.bankdroid.PropertyObject";
	final static String EXTRA_PROPERTY_DEFAULT_LABELS = "com.bankdroid.PropertyDefaultLabels";
	final static String EXTRA_PROPERTY_DEFAULT_VALUES = "com.bankdroid.PropertyDefaultValues";
	final static String EXTRA_ACTIVITY_TITLE = "com.bankdroid.ActivityTitle";
}
