package bankdroid.start.auth;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import bankdroid.start.R;
import bankdroid.start.plugin.PluginManager;

import com.csaba.connector.model.Customer;

public class CustomerAdapter extends BaseAdapter
{
	private final Customer[] customers;

	public CustomerAdapter( final Customer[] customers )
	{

		this.customers = customers;
	}

	@Override
	public int getCount()
	{
		return customers.length;
	}

	@Override
	public Object getItem( final int position )
	{
		return customers[position];
	}

	public Customer getCustomer( final int position )
	{
		return customers[position];
	}

	@Override
	public long getItemId( final int position )
	{
		return position;
	}

	@Override
	public View getView( final int position, final View contentView, final ViewGroup parent )
	{
		View view = contentView;
		if ( view == null )
		{
			view = View.inflate(parent.getContext(), R.layout.customeritem, null);
		}

		final Customer customer = customers[position];
		( (TextView) view.findViewById(R.id.customerName) ).setText(customer.getName());
		( (TextView) view.findViewById(R.id.customerLoginId) ).setText(customer.getLoginId());
		( (ImageView) view.findViewById(R.id.bankLogo) ).setImageDrawable(PluginManager.getIconDrawable(customer
				.getBank().getLargeIcon()));

		return view;
	}

}
