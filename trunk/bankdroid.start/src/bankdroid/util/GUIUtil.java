package bankdroid.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import bankdroid.start.Codes;
import bankdroid.start.R;

import com.csaba.connector.model.Account;

public class GUIUtil implements Codes
{
	private static int negativeColor = -1;
	private static int positiveColor = -1;
	private static int zeroColor = -1;

	public final static String HTML_NEGATIVE_COLOR = "#FF0000";
	public final static String HTML_POSITIVE_COLOR = "#00FF00";
	public final static String HTML_ZERO_COLOR = "#000000";

	public static void setTitle( final Activity activity, final int titleId )
	{
		final TextView view = (TextView) activity.findViewById(R.id.titleView);
		view.setText(titleId);
	}

	public static void setTitle( final Activity activity, final String title )
	{
		final TextView view = (TextView) activity.findViewById(R.id.titleView);
		view.setText(title);
	}

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

	public static void fatalError( final Context context, final Exception e )
	{
		final Toast toast = Toast.makeText(context, context.getString(R.string.errSystemError) + "\n" + e,
				Toast.LENGTH_LONG);
		toast.show();
		Log.e(TAG, "Fatal error occured.", e);

	}

}
