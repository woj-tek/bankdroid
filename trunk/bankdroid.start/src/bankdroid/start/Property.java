/**
 * 
 */
package bankdroid.start;

import java.io.Serializable;
import java.util.Date;

import bankdroid.util.GUIUtil;

import com.csaba.connector.model.Account;
import com.csaba.util.Formatters;

public class Property implements Serializable
{
	private static final long serialVersionUID = 7615845862507025576L;

	private final String name;
	private final String valueString;
	private final Object value;

	public String getName()
	{
		return name;
	}

	public String getValueString()
	{
		return valueString;
	}

	public Property( final String name, final Object value )
	{
		super();
		this.name = name;
		this.value = value;
		this.valueString = convertObjectProperty(value);
	}

	private String convertObjectProperty( final Object value )
	{
		if ( value == null )
		{
			return null;
		}
		else if ( value instanceof Date )
		{
			return Formatters.getShortDateFormat().format(value);
		}
		else if ( value instanceof Account )
		{
			return GUIUtil.getAccountName((Account) value);
		}
		else
		{
			return value.toString();
		}

	}

	public Object getValue()
	{
		return value;
	}

}