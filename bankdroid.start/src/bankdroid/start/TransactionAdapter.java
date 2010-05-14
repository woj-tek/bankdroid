package bankdroid.start;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import bankdroid.util.Formatters;
import bankdroid.util.GUIUtil;

import com.csaba.connector.model.Amount;
import com.csaba.connector.model.HistoryItem;

/**
 * @author Gabe
 */
public class TransactionAdapter extends BaseAdapter
{
	private final List<HistoryItem> items;

	public final Comparator<HistoryItem> AMOUNT_COMPARATOR = new Comparator<HistoryItem>()
	{

		@Override
		public int compare( final HistoryItem object1, final HistoryItem object2 )
		{
			return (int) ( object1.getAmount().getAmount() - object2.getAmount().getAmount() );
		}
	};

	public final Comparator<HistoryItem> DATE_COMPARATOR = new Comparator<HistoryItem>()
	{

		@Override
		public int compare( final HistoryItem object1, final HistoryItem object2 )
		{
			return object1.getDate().compareTo(object2.getDate());
		}
	};

	public TransactionAdapter()
	{
		items = new ArrayList<HistoryItem>();
	}

	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public Object getItem( final int position )
	{
		return items.get(position);
	}

	@Override
	public long getItemId( final int position )
	{
		return position;
	}

	@Override
	public View getView( final int position, View contentView, final ViewGroup parent )
	{
		final Context context = parent.getContext();
		if ( contentView == null )
		{
			contentView = View.inflate(context, R.layout.tranitem, null);
		}

		final HistoryItem item = items.get(position);

		final TextView amount = (TextView) contentView.findViewById(R.id.amount);
		amount.setText(item.getAmount().toString());
		amount.setTextColor(GUIUtil.getColor(context, item.getAmount().getAmount()));

		( (TextView) contentView.findViewById(R.id.date) ).setText(Formatters.getShortDateFormat().format(
				item.getDate()));
		( (TextView) contentView.findViewById(R.id.description) ).setText(item.getDescription());

		return contentView;
	}

	public void setItems( final HistoryItem[] items )
	{
		if ( items == null )
			return;

		this.items.clear();
		for ( final HistoryItem item : items )
		{
			this.items.add(item);
		}

		Collections.sort(this.items, DATE_COMPARATOR);

		notifyDataSetChanged();
	}

	public void addItems( final HistoryItem[] items )
	{
		if ( items == null || items.length == 0 )
			return;

		for ( final HistoryItem item : items )
		{
			this.items.add(item);
		}

		Collections.sort(this.items, DATE_COMPARATOR);

		notifyDataSetChanged();
	}

	public Amount getCredits()
	{
		Amount amount = null;
		for ( final HistoryItem item : items )
		{
			final Amount other = item.getAmount();
			if ( other.getAmount() > 0 )
			{
				if ( amount == null )
				{
					amount = new Amount(other);
				}
				else
				{
					if ( !amount.getCurrency().equals(other.getCurrency()) )
						throw new IllegalArgumentException("Currency mismatch in transaction list.");
					amount.setAmount(amount.getAmount() + other.getAmount());
				}
			}
		}
		return amount;
	}

	public Amount getDebits()
	{
		Amount amount = null;
		for ( final HistoryItem item : items )
		{
			final Amount other = item.getAmount();
			if ( other.getAmount() < 0 )
			{
				if ( amount == null )
				{
					amount = new Amount(other);
				}
				else
				{
					if ( !amount.getCurrency().equals(other.getCurrency()) )
						throw new IllegalArgumentException("Currency mismatch in transaction list.");
					amount.setAmount(amount.getAmount() + other.getAmount());
				}
			}
		}
		return amount;
	}

}
