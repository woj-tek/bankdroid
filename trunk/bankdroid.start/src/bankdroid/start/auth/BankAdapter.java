package bankdroid.start.auth;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import bankdroid.start.R;
import bankdroid.start.plugin.PluginManager;

import com.csaba.connector.model.Bank;

public class BankAdapter extends BaseAdapter
{
	private final List<Bank> banks;
	private Bank dummyBank;
	private boolean dummyBankShown = false;

	public BankAdapter( final Bank[] banks )
	{

		this.banks = new ArrayList<Bank>();

		for ( final Bank bank : banks )
		{
			if ( bank.getId().equals("DUMMY") )
			{
				dummyBank = bank;
			}
			else
			{
				this.banks.add(bank);
			}
		}
	}

	@Override
	public int getCount()
	{
		return banks.size();
	}

	@Override
	public Object getItem( final int position )
	{
		return banks.get(position);
	}

	@Override
	public long getItemId( final int arg0 )
	{
		return arg0;
	}

	@Override
	public View getView( final int position, final View contentView, final ViewGroup parent )
	{
		View view = contentView;
		if ( view == null )
		{
			view = View.inflate(parent.getContext(), R.layout.onerow_icon, null);
		}

		final TextView textView = (TextView) view;
		textView.setCompoundDrawablesWithIntrinsicBounds(PluginManager.getIconDrawable(banks.get(position)
				.getLargeIcon()), null, null, null);
		textView.setText(banks.get(position).getName());
		return view;
	}

	public void setDummyAvailable( final boolean dummyBankShown )
	{
		if ( dummyBank == null )
		{
			return;
		}

		if ( this.dummyBankShown ^ dummyBankShown )
		{
			this.dummyBankShown = dummyBankShown;
			if ( this.dummyBankShown )
			{
				banks.add(dummyBank);
			}
			else
			{
				banks.remove(dummyBank);
			}

			notifyDataSetChanged();
		}
	}
}
