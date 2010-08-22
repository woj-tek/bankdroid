package bankdroid.start.auth;

import android.app.Activity;
import android.widget.TextView;
import bankdroid.start.R;
import bankdroid.start.plugin.PluginManager;

import com.csaba.connector.model.Bank;

public class AuthUtil
{

	static void setSelectedBank( final Activity act, final Bank selected )
	{

		final TextView bank = (TextView) act.findViewById(R.id.bankSelected);
		bank.setText(selected.getName());
		bank.setCompoundDrawables(PluginManager.getIconDrawable(selected.getLargeIcon()), null, null, null);
	}
}
