package bankdroid.start;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.csaba.connector.model.AbstractRemoteObject;
import com.csaba.connector.model.Account;
import com.csaba.connector.model.Customer;
import com.csaba.connector.model.HistoryItem;

public class PropertyHelper
{

	private PropertyHelper()
	{
		// do not instantiate this clas
	}

	public static Property[] getProperties( final Context context, final Account account )
	{
		final List<Property> result = new ArrayList<Property>();

		result.add(new Property(context.getString(R.string.accountName), account.getName()));
		result.add(new Property(context.getString(R.string.accountNumber), account.getNumber()));
		result.add(new Property(context.getString(R.string.availableBalance), account.getAvailableBalance()));
		result.add(new Property(context.getString(R.string.bookedBalance), account.getBookedBalance()));
		result.add(new Property(context.getString(R.string.accountType), account.getType()));
		result.add(new Property(context.getString(R.string.accountIBAN), account.getIBAN()));

		finalizeResult(account, result);
		return result.toArray(new Property[result.size()]);
	}

	public static Property[] getProperties( final Context context, final HistoryItem item )
	{
		final List<Property> result = new ArrayList<Property>();

		result.add(new Property(context.getString(R.string.accountNumber), item.getOwner()));
		result.add(new Property(context.getString(R.string.transactionDate), item.getDate()));
		result.add(new Property(context.getString(R.string.transactionAmount), item.getAmount()));
		result.add(new Property(context.getString(R.string.transactionDescription), item.getDescription()));
		result.add(new Property(context.getString(R.string.transactionBalance), item.getBalance()));

		finalizeResult(item, result);
		return result.toArray(new Property[result.size()]);
	}

	public static Property[] getProperties( final Context context, final Customer customer )
	{
		final List<Property> result = new ArrayList<Property>();

		result.add(new Property(context.getString(R.string.customerId), customer.getId()));
		result.add(new Property(context.getString(R.string.loginId), customer.getLoginId()));
		result.add(new Property(context.getString(R.string.customerName), customer.getName()));

		finalizeResult(customer, result);
		return result.toArray(new Property[result.size()]);
	}

	private static void finalizeResult( final AbstractRemoteObject object, final List<Property> result )
	{
		final String[] names = object.getRemotePropertyNames();
		for ( final String name : names )
		{
			final String label = object.getLocalizedName(name) + ":";
			final Object value = object.getRemoteProperty(name);
			result.add(new Property(label, value));
		}

		for ( final Iterator<Property> it = result.iterator(); it.hasNext(); )
		{
			final Property property = it.next();
			if ( property.getValue() == null )
				it.remove();
		}
	}

	public static Property[] convertArray( final Object[] source )
	{
		final Property[] target = new Property[source.length];
		for ( int i = 0; i < target.length; i++ )
		{
			target[i] = (Property) source[i];
		}
		return target;
	}
}
