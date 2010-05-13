package bankdroid.start;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csaba.connector.model.HistoryItem;

public class TransactionAdapter extends BaseAdapter
{
	private final List<HistoryItem> items;

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

		( (TextView) contentView.findViewById(R.id.amount) ).setText(item.getAmount().toString());
		( (TextView) contentView.findViewById(R.id.date) ).setText(item.getDate().toLocaleString()); //FIXME set format
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

		notifyDataSetChanged();
	}

}
