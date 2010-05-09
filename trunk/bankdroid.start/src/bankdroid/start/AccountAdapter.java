/**
 * 
 */
package bankdroid.start;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csaba.connector.model.Account;

class AccountAdapter extends BaseAdapter
{
	private final Account[] accounts;

	private int negativeColor = -1;
	private int positiveColor = -1;
	private int zeroColor = -1;

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
		final Context context = parent.getContext();
		if ( contentView == null )
		{
			contentView = View.inflate(context, R.layout.accountitem, null);
		}

		final Account acc = accounts[position];

		( (TextView) contentView.findViewById(R.id.accountName) ).setText(acc.getName());
		( (TextView) contentView.findViewById(R.id.accountNumber) ).setText(acc.getNumber());

		final TextView availableBalance = (TextView) contentView.findViewById(R.id.availableBalance);
		availableBalance.setText(acc.getAvailableBalance().toString());
		availableBalance.setTextColor(getColor(context, acc.getAvailableBalance().getAmount()));

		final TextView bookedBalance = (TextView) contentView.findViewById(R.id.bookedBalance);
		bookedBalance.setText(acc.getBookedBalance().toString());
		bookedBalance.setTextColor(getColor(context, acc.getBookedBalance().getAmount()));

		return contentView;
	}

	private int getColor( final Context context, final double value )
	{
		if ( value < 0 )
		{
			return negativeColor == -1 ? negativeColor = context.getResources().getColor(R.color.negativeAmount)
					: negativeColor;
		}
		else if ( value > 0 )
		{
			return positiveColor == -1 ? positiveColor = context.getResources().getColor(R.color.positiveAmount)
					: positiveColor;
		}
		return zeroColor == -1 ? zeroColor = context.getResources().getColor(R.color.zeroAmount) : zeroColor;
	}

}