package bankdroid.start.auth;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import bankdroid.start.Codes;

import com.csaba.util.encryption.EncryptedStore;

public class SecureRegistry implements Codes
{
	private final EncryptedStore store;

	private SecureRegistry( final Context context ) throws IOException, GeneralSecurityException,
			ClassNotFoundException
	{
		final String key = getKey(context);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		final String encryptedData = preferences.getString(PREF_ENCRYPTED_STORE, "");
		store = EncryptedStore.getInstance();
		store.setContent(encryptedData);
		store.setPassword(key.toCharArray());
	}

	private String getKey( final Context context )
	{
		final String imei = ( (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE) ).getDeviceId();
		if ( TextUtils.isEmpty(imei) )
		{
			throw new IllegalStateException("Cannot get seed to open the encrypted store.");
		}
		return imei;
	}

	public String[] getKeysStartWith( final String keyStart )
	{
		return store.getKeysStartWith(keyStart);
	}

	public void putValue( final String key, final Serializable value )
	{
		store.putValue(key, value);
	}

	public Serializable getValue( final String key )
	{
		return store.getValue(key);
	}

	public String getString( final String key )
	{
		return (String) store.getValue(key);
	}

	public void commit( final Context context ) throws IOException, GeneralSecurityException
	{
		final String stringValue = store.toBASE64String();

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor = preferences.edit();
		editor.putString(PREF_ENCRYPTED_STORE, stringValue);
		editor.commit();
	}

	private static SecureRegistry instance;

	public static SecureRegistry getInstance( final Context context ) throws IOException, GeneralSecurityException,
			ClassNotFoundException
	{
		if ( instance == null )
		{
			instance = new SecureRegistry(context);
		}
		return instance;
	}
}
