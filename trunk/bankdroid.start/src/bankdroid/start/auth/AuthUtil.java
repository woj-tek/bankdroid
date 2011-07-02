package bankdroid.start.auth;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import bankdroid.start.Codes;
import bankdroid.start.R;

import com.csaba.connector.BankServiceFactory;
import com.csaba.connector.ServiceException;
import com.csaba.connector.model.Bank;
import com.csaba.connector.model.Customer;

public class AuthUtil implements Codes
{

	private static final String REGKEY_REMOTE_PREFIX = "remote/";
	private static final String REGKEY_NAME = "name";
	private static final String REGKEY_PASSWORD = "password";
	private static final String REGKEY_LOGINID = "loginId";
	private static final String REGKEY_BANK = "bank";

	static void setSelectedBank( final Activity act, final Bank selected )
	{

		final TextView bank = (TextView) act.findViewById(R.id.bankSelected);
		bank.setText(selected.getName());
		//bank.setCompoundDrawables(PluginManager.getIconDrawable(selected.getLargeIcon()), null, null, null);

		//remove icon
		bank.setCompoundDrawables(null, null, null, null);
		bank.setCompoundDrawablePadding(0);
	}

	static void storeCustomer( final SecureRegistry registry, final int index, final Customer customer,
			final String[] propertyList, final boolean storePassword )
	{
		Integer idSeq = index;
		if ( index < 1 )
		{
			//generate new ID for customer
			idSeq = (Integer) registry.getValue(REG_CUSTOMERID_SEQ);
			idSeq = idSeq == null ? 1 : idSeq + 1;
			registry.putValue(REG_CUSTOMERID_SEQ, idSeq);
		}
		else
		{
			removeCustomer(registry, index);
		}

		//save store password property to make it easy to inquire it later
		customer.setRemoteProperty(RP_STORE_PASSWORD, storePassword);
		customer.setRemoteProperty(RP_REGISTRY_ID, idSeq);

		//store basic data of customer

		final String keyPrefix = REG_CUSTOMER_PREFIX + String.valueOf(idSeq) + "/";
		registry.putValue(keyPrefix + REGKEY_BANK, customer.getBank().getId());
		registry.putValue(keyPrefix + REGKEY_LOGINID, customer.getLoginId());
		if ( storePassword )
			registry.putValue(keyPrefix + REGKEY_PASSWORD, customer.getPassword());
		registry.putValue(keyPrefix + REGKEY_NAME, customer.getName());

		//store remote properties
		if ( propertyList != null )
		{
			for ( final String property : propertyList )
			{
				if ( customer.isRemotePropertySet(property) )
				{
					registry.putValue(keyPrefix + REGKEY_REMOTE_PREFIX + property,
							(String) customer.getRemoteProperty(property));
				}
			}
		}

		cache = null;
	}

	static void removeCustomer( final SecureRegistry registry, final int index )
	{
		//remove original values
		final String[] keys = registry.getKeysStartWith(REG_CUSTOMER_PREFIX + String.valueOf(index) + "/");
		for ( final String key : keys )
		{
			registry.remove(key);
		}

		cache = null;
	}

	private static Customer[] cache;

	static Customer[] restoreCustomers( final SecureRegistry registry )
	{
		if ( cache != null )
			return cache;

		final Integer idSeq = (Integer) registry.getValue(REG_CUSTOMERID_SEQ);
		if ( idSeq == null || idSeq < 1 )
		{
			return null;
		}
		Bank[] availableBanks;
		try
		{
			availableBanks = BankServiceFactory.getAvailableBanks();
		}
		catch ( final ServiceException e )
		{
			Log.e(TAG, "Failed to initialize bank codes.", e);
			return null;
		}

		final List<Customer> result = new ArrayList<Customer>();

		customerIteration: for ( int i = 1; i <= idSeq; i++ )
		{
			final String prefix = REG_CUSTOMER_PREFIX + i + "/";
			final String[] keys = registry.getKeysStartWith(prefix);
			if ( keys != null && keys.length > 0 )
			{
				final Customer cust = new Customer();
				for ( final String key : keys )
				{
					String suffix = key.substring(prefix.length());
					if ( REGKEY_BANK.equals(suffix) )
					{
						final String bankId = registry.getString(key);
						boolean found = false;
						for ( final Bank bank : availableBanks )
						{
							if ( bank.getId().equals(bankId) )
							{
								cust.setBank(bank);
								found = true;
								break;
							}
						}
						if ( !found )
						{
							Log.e(TAG, "Bank code was not found on this configuration: " + bankId);
							continue customerIteration;
						}
					}
					else if ( REGKEY_LOGINID.equals(suffix) )
					{
						cust.setLoginId(registry.getString(key));
					}
					else if ( REGKEY_NAME.equals(suffix) )
					{
						cust.setName(registry.getString(key));
					}
					else if ( REGKEY_PASSWORD.equals(suffix) )
					{
						cust.setPassword(registry.getString(key));
					}
					else if ( suffix.startsWith(REGKEY_REMOTE_PREFIX) )
					{
						suffix = suffix.substring(REGKEY_REMOTE_PREFIX.length());
						cust.setRemoteProperty(suffix, registry.getString(key));
					}
					else
					{
						Log.w(TAG, "Invalid customer property: " + key);
					}
				}
				cust.setRemoteProperty(RP_REGISTRY_ID, i);
				result.add(cust);
			}

		}

		if ( result.size() == 0 )
			return null;

		return result.toArray(new Customer[result.size()]);
	}
}
