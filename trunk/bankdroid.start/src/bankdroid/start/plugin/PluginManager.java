package bankdroid.start.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import bankdroid.start.Codes;

import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ClassEnumerationProvider;
import com.csaba.connector.ServicePluginConfiguration;
import com.csaba.connector.bha.BHAPluginConfiguration;
import com.csaba.connector.dummy.DummyPluginConfiguration;

public class PluginManager implements Codes
{

	static
	{
		final Set<ServicePluginConfiguration> plugins = new LinkedHashSet<ServicePluginConfiguration>();
		plugins.add(new DummyPluginConfiguration());
		plugins.add(new BHAPluginConfiguration());
		//XXX plugins.add(new OTPPluginConfiguration());

		BankServiceFactory.setProvider(new ClassEnumerationProvider(plugins));
	}

	public static void init()
	{
		// do nothing
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

}
