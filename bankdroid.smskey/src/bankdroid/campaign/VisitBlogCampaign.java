package bankdroid.campaign;

import java.util.Date;

import android.content.Context;
import android.view.View;
import bankdroid.smskey.R;

public class VisitBlogCampaign implements Campaign
{

	@Override
	public View getView( final Context context )
	{
		return View.inflate(context, R.layout.campaign_visitblog, null);
	}

	//FIXME implement hit detection
	@Override
	public boolean isActive( final Date lastShown, final int numberOfAppearance, final int hitCount, final int codeCount )
	{
		return hitCount == 0;
	}

	@Override
	public void hit( final Context context )
	{
		// TODO Auto-generated method stub

	}
}
