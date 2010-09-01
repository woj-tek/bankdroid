package bankdroid.start;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Gabe
 */
public class PropertyAdapter extends BaseAdapter
{
	private final Property[] properties;

	public PropertyAdapter( final Property[] properties )
	{
		this.properties = properties;

	}

	@Override
	public int getCount()
	{
		return properties.length;
	}

	@Override
	public Object getItem( final int position )
	{
		return properties[position];
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
			view = View.inflate(parent.getContext(), R.layout.propertyitem, null);
		}

		final Property prop = (Property) getItem(position);
		( (TextView) view.findViewById(R.id.propertyName) ).setText(prop.getName());
		( (TextView) view.findViewById(R.id.propertyValue) ).setText(prop.getValueString());

		return view;
	}

}
