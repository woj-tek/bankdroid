package bankdroid.util;

import android.content.Context;
import bankdroid.start.R;

public class GUIUtil
{
	private static int negativeColor = -1;
	private static int positiveColor = -1;
	private static int zeroColor = -1;

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

}
