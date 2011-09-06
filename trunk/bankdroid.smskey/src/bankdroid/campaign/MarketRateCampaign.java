package bankdroid.campaign;

import java.util.Date;

import android.content.Context;
import android.view.View;
import bankdroid.smskey.R;

public class MarketRateCampaign implements Campaign
{

	@Override
	public View getView( final Context context )
	{
		return View.inflate(context, R.layout.campaign_rateapp, null);
	}

	@Override
	public boolean isActive( final Date lastShown, final int numberOfAppearance, final int hitCount, final int codeCount )
	{
		// FIXME filtering logic
		return true;
	}

}
