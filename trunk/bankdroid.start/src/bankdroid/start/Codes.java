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
	final static String PREF_SAVE_LAST_LOGIN = "bankdroid.start.SaveLastLogin";
	final static String PREF_SHOW_DUMMY_BANK = "bankdroid.start.ShowDummyBank";
	final static String PREF_SESSION_TIMEOUT = "bankdroid.start.SessionTimeout";

	final static String PREF_ENCRYPTED_STORE = "bankdroid.start.EncryptedStore";

	//REGISTRY KEYS
	final static String REG_CUSTOMER_PREFIX = "/customer/";
	final static String REG_CUSTOMERID_SEQ = "/customeridseq";

	//REMOTE PROPERTIES FOR REMOTE OBJECTS
	final static String RP_REGISTRY_ID = "bankdroid.start.registryId";

	//DEFAULTS
	final static String DEFAULT_SESSION_TIMEOUT = "3";

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
	final static String EXTRA_CUSTOMER = "com.bankdroid.Customer";
}
