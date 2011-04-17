/**
 * 
 */
package bankdroid.start;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import bankdroid.util.GUIUtil;

import com.csaba.connector.model.Account;
import com.csaba.connector.model.Amount;

class AccountAdapter extends BaseAdapter
{
	private final Account[] accounts;

	public AccountAdapter( final Account[] accounts )
	{
		this.accounts = accounts;
	}

	@Override
	public int getCount()
	{
		return accounts.length;
	}

	@Override
	public Object getItem( final int position )
	{
		return accounts[position];
	}

	public Object getItemById( final long id )
	{
		for ( final Account account : accounts )
		{
			if ( account.hashCode() == id )
				return account;
		}
		throw new IllegalArgumentException("Invalid account ID: " + id);
	}

	@Override
	public long getItemId( final int position )
	{
		return accounts[position].hashCode();
	}

	@Override
	public View getView( final int position, View contentView, final ViewGroup parent )
	{
		final Context context = parent.getContext();
		if ( contentView == null )
		{
			contentView = View.inflate(context, R.layout.accountitem, null);
		}

		final Account acc = accounts[position];

		( (TextView) contentView.findViewById(R.id.accountName) ).setText(GUIUtil.getAccountName(acc));

		final TextView availableBalance = (TextView) contentView.findViewById(R.id.availableBalance);
		final Amount availableBalanceValue = acc.getAvailableBalance();
		if ( availableBalanceValue != null )
		{
			availableBalance.setText(availableBalanceValue.toString());
			availableBalance.setTextColor(GUIUtil.getColor(context, availableBalanceValue.getAmount()));
			availableBalance.setVisibility(View.VISIBLE);
		}
		else
		{
			availableBalance.setVisibility(View.GONE);
		}

		final TextView bookedBalance = (TextView) contentView.findViewById(R.id.bookedBalance);
		final Amount bookedBalanceValue = acc.getBookedBalance();
		if ( bookedBalanceValue != null )
		{
			bookedBalance.setText(bookedBalanceValue.toString());
			bookedBalance.setTextColor(GUIUtil.getColor(context, bookedBalanceValue.getAmount()));
			bookedBalance.setVisibility(View.VISIBLE);
		}
		else
		{
			bookedBalance.setVisibility(View.GONE);
		}

		return contentView;
	}

}