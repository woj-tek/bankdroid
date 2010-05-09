package bankdroid.start;

public interface Codes
{
	final static String TAG = "Start";

	final static int SERVICE_PROCESS = 1;
	final static int SERVICE_FAILED = 2;

	final static String SERVICE_EXCEPTION = "SERVICE_EXCEPTION";

	//PREFERENCES
	final static String PREF_LAST_BANK = "bankdroid.start.LastBank";
	final static String PREF_LAST_LOGINID = "bankdroid.start.LastLoginId";
	final static String PREF_LAST_PASSWORD = "bankdroid.start.LastPassword";
	final static String PREF_SAVE_LAST_LOGIN = "bankdroid.start.SaveLastLogin";
	final static String PREF_SAVE_PASSWORD = "bankdroid.start.SavePassword";

	//DEFAULTS
	final static String DEFAULT_LOGINID = "local";
	final static String DEFAULT_PASSWORD = "password";
}
