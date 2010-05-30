package bankdroid.start;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csaba.connector.model.AbstractRemoteObject;

/**
 * @author Gabe
 */
public class PropertyAdapter extends BaseAdapter
{
	private final AbstractRemoteObject object;
	private final String[] names;
	private final String[] defaultLabels;
	private final String[] defaultValues;
	private final int defaultLength;

	public PropertyAdapter( final AbstractRemoteObject object, final String[] defaultLabels,
			final String[] defaultValues )
	{
		this.object = object;
		this.defaultLabels = defaultLabels;
		this.defaultValues = defaultValues;

		this.names = object.getRemotePropertyNames();
		this.defaultLength = defaultLabels.length;
	}

	@Override
	public int getCount()
	{
		return names.length + defaultLabels.length;
	}

	@Override
	public Object getItem( final int position )
	{
		if ( position >= defaultLength )
		{
			return new Property(object.getLocalizedName(names[position - defaultLength]) + ":", object
					.getRemoteProperty(names[position - defaultLength]));
		}
		return new Property(defaultLabels[position], defaultValues[position]);

	}

	@Override
	public long getItemId( final int position )
	{
		if ( position >= defaultLength )
		{
			return names[position - defaultLength].hashCode();
		}
		return defaultLabels[position].hashCode();

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
		( (TextView) view.findViewById(R.id.propertyValue) ).setText(prop.getValue().toString());

		return view;
	}

	class Property
	{
		String name;
		Object value;

		public String getName()
		{
			return name;
		}

		public Object getValue()
		{
			return value;
		}

		public Property( final String name, final Object value )
		{
			super();
			this.name = name;
			this.value = value;
		}

	}

}
