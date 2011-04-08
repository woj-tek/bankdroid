package bankdroid.soda;

import android.net.Uri;

public interface Codes
{
	public static final String DEFAULT_COUNTRY = "HU";

	public static final String FACEBOOK_URL = "http://on.fb.me/bd_facse";
	public static final String TWITTER_URL = "http://twitter.com/bankdroid";
	public static final String GMAIL_URL = "mailto:info@bankdroid.info";
	public static final String URL_PROJECT_HOME = "http://bit.ly/4Ys9pQ";
	public static final String URL_HELP = "http://bit.ly/emal4O";
	public static final String URL_HOME_PAGE = "http://bit.ly/hVsuzF";

	public static final String BANKDROID_SODA_ADDRESS = "bankdroid.soda.Address";
	public static final String BANKDROID_SODA_SMSMESSAGE = "bankdroid.soda.SMSMessage";
	public static final String BANKDROID_SODA_SMSTIMESTAMP = "bankdroid.soda.SMSTimestamp";

	public static final String BANKDROID_SODA_BANK = "bankdroid.soda.Bank";

	public static final String BANKDROID_SODA_SMSCODE = "bankdroid.soda.SMSCode";

	public static final String PREF = "bankdroid.soda";
	public static final String PREF_NOTIFICATION = "bankdroid.soda.Notification";
	public static final String PREF_KEEP_SMS = "bankdroid.soda.KeepSMS";
	public static final String PREF_KEEP_SCREEN_ON = "bankdroid.soda.KeepScreenOn";
	public static final String PREF_RESET_DB = "bankdroid.soda.ResetDb";
	public static final String PREF_UNLOCK_SCREEN = "bankdroid.soda.UnlockScreen";
	public static final String PREF_AUTO_COPY = "bankdroid.soda.AutoCopy";
	public static final String PREF_CODE_COUNT = "bankdroid.soda.CodeCount";

	public static final boolean DEFAULT_NOTIFICATION = false;
	public static final boolean DEFAULT_KEEP_SMS = false;
	public static final boolean DEFAULT_KEEP_SCREEN_ON = true;
	public static final boolean DEFAULT_UNLOCK_SCREEN = true;
	public static final boolean DEFAULT_AUTO_COPY = true;

	public static final String TAG = "SODA";

	public static final int NOTIFICATION_ID = 7632;

	public static final String PROVIDER_AUTHORITY = "bankdroid.soda.Bank";

	public static final String ACTION_DISPLAY = "bankdroid.soda.action.Display";
	public static final String ACTION_REDISPLAY = "bankdroid.soda.action.Redisplay";

	//activity results
	public static final int REQUEST_EMAIL_SEND = 1001;
	public static final int REQUEST_SELECT_SMS = 1002;

	/**
	 * The MIME type of {@link #CONTENT_URI} providing a directory of banks.
	 */
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/bankdroid.soda.bank";

	/**
	 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single bank.
	 */
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/bankdroid.soda.bank";

	/**
	 * The content:// style URL for this table.
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://bankdroid.soda.Bank/banks");

}
