/**
 * 
 */
package bankdroid.start;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csaba.connector.model.Account;

class AccountAdapter extends BaseAdapter
{
	Account[] accounts;

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
	public Object getItem( final int arg0 )
	{
		return accounts[arg0];
	}

	@Override
	public long getItemId( final int arg0 )
	{
		return accounts[arg0].hashCode();
	}

	@Override
	public View getView( final int position, View contentView, final ViewGroup parent )
	{
		if ( contentView == null )
		{
			contentView = View.inflate(parent.getContext(), R.layout.accountitem, null);
		}

		final Account acc = accounts[position];

		( (TextView) contentView.findViewById(R.id.accountName) ).setText(acc.getName());
		( (TextView) contentView.findViewById(R.id.accountNumber) ).setText(acc.getNumber());
		( (TextView) contentView.findViewById(R.id.availableBalance) )
				.setText(acc.getAvailableBalance().toString());
		( (TextView) contentView.findViewById(R.id.bookedBalance) ).setText(acc.getBookedBalance().toString());
		return contentView;
	}

}