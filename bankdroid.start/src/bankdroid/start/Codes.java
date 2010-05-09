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
}
