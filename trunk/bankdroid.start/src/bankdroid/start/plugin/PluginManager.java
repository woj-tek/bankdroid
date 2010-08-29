package bankdroid.start.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import bankdroid.start.Codes;
import bankdroid.start.auth.AuthStartActivity;
import bankdroid.util.AndroidFormatter;
import bankdroid.util.AndroidHandler;

import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ClassEnumerationProvider;
import com.csaba.connector.ServicePluginConfiguration;
import com.csaba.connector.bha.BHAPluginConfiguration;
import com.csaba.connector.dummy.DummyPluginConfiguration;
import com.csaba.connector.model.Bank;
import com.csaba.connector.otp.OTPPluginConfiguration;
import com.csaba.util.LoggerHelper;

public class PluginManager implements Codes
{
	private static final Set<ServicePluginConfiguration> plugins = new LinkedHashSet<ServicePluginConfiguration>();
	static
	{
		plugins.add(new DummyPluginConfiguration());
		plugins.add(new BHAPluginConfiguration());
		plugins.add(new OTPPluginConfiguration());

		BankServiceFactory.setProvider(new ClassEnumerationProvider(plugins));
	}

	private static boolean initted = false;

	public static void init()
	{
		if ( !initted )
		{
			// initialize Java util logging
			final Level level = Level.FINE;
			LoggerHelper.initLogger(level);
			final AndroidHandler handler = new AndroidHandler(TAG);
			handler.setLevel(level);
			handler.setFormatter(new AndroidFormatter(false));
			LoggerHelper.setLogHandler(handler);
			initted = true;
		}
	}

	private static final Map<URL, Drawable> bankIcons = new HashMap<URL, Drawable>();

	public static Drawable getIconDrawable( final URL url )
	{
		if ( !bankIcons.containsKey(url) )
		{
			try
			{
				bankIcons.put(url, new BitmapDrawable(url.openConnection().getInputStream()));
			}
			catch ( final IOException e )
			{
				Log.e(TAG, "Failed to load image resource: " + url, e);
				return null;
			}
		}
		return bankIcons.get(url);
	}

	public static Class<?> getAuthActivityClass( final Bank bank ) throws ClassNotFoundException
	{
		String prefix = null;
		for ( final ServicePluginConfiguration plugin : plugins )
		{
			if ( plugin.getBank().equals(bank) )
			{
				prefix = plugin.getServicePrefix();
				break;
			}
		}

		if ( prefix == null )
			throw new IllegalArgumentException("No plugin configuration for bank " + bank);

		return Class.forName(AuthStartActivity.class.getPackage().getName() + "." + prefix + "AuthActivity");
	}

}
