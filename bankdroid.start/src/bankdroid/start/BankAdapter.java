package bankdroid.start;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bankdroid.start.plugin.PluginManager;

import com.csaba.connector.model.Bank;

public class BankAdapter extends BaseAdapter
{
	private final Bank[] banks;

	public BankAdapter( final Bank[] banks )
	{
		this.banks = banks;
	}

	@Override
	public int getCount()
	{
		return banks.length;
	}

	@Override
	public Object getItem( final int arg0 )
	{
		return banks[arg0];
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
			view = View.inflate(parent.getContext(), R.layout.bankitem, null);
		}

		( (ImageView) view.findViewById(R.id.bankLogo) ).setImageDrawable(PluginManager.getIconDrawable(banks[position]
				.getLargeIcon()));
		( (TextView) view.findViewById(R.id.bankName) ).setText(banks[position].getName());
		return view;
	}

}
