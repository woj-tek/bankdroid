package bankdroid.util;

import android.content.Context;
import bankdroid.start.R;

import com.csaba.connector.model.Account;

public class GUIUtil
{
	private static int negativeColor = -1;
	private static int positiveColor = -1;
	private static int zeroColor = -1;

	public final static String HTML_NEGATIVE_COLOR = "#BB0000";
	public final static String HTML_POSITIVE_COLOR = "#00BB00";
	public final static String HTML_ZERO_COLOR = "#000000";

	public static int getColor( final Context context, final double value )
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

	public static String getHtmlColor( final double value )
	{
		if ( value < 0 )
		{
			return HTML_NEGATIVE_COLOR;
		}
		else if ( value > 0 )
		{
			return HTML_POSITIVE_COLOR;
		}
		return HTML_ZERO_COLOR;
	}

	public static String getAccountName( final Account account )
	{
		final String name = account.getName();
		return name == null || name.equals("") ? account.getNumber() : name;
	}
}
