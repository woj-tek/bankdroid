package bankdroid.start;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csaba.connector.model.AbstractRemoteObject;
import com.csaba.util.Formatters;

/**
 * @author Gabe
 */
public class PropertyAdapter extends BaseAdapter
{
	private final List<Property> properties = new ArrayList<Property>();

	public PropertyAdapter( final AbstractRemoteObject object, final String[] defaultLabels,
			final String[] defaultValues )
	{
		for ( int i = 0; i < defaultValues.length; i++ )
		{
			if ( defaultValues[i] != null )
			{
				properties.add(new Property(defaultLabels[i], defaultValues[i]));
			}
		}

		final String[] names = object.getRemotePropertyNames();
		for ( final String name : names )
		{
			final String label = object.getLocalizedName(name) + ":";
			final Object value = object.getRemoteProperty(name);
			String valueString;
			if ( value instanceof Date )
			{
				valueString = Formatters.getShortDateFormat().format(value);
			}
			else
			{
				valueString = value.toString();
			}
			properties.add(new Property(label, valueString));
		}
	}

	@Override
	public int getCount()
	{
		return properties.size();
	}

	@Override
	public Object getItem( final int position )
	{
		return properties.get(position);

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
		( (TextView) view.findViewById(R.id.propertyValue) ).setText(prop.getValue());

		return view;
	}

	class Property
	{
		String name;
		String value;

		public String getName()
		{
			return name;
		}

		public String getValue()
		{
			return value;
		}

		public Property( final String name, final String value )
		{
			super();
			this.name = name;
			this.value = value;
		}

	}

}
