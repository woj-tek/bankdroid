package bankdroid.campaign;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import bankdroid.smskey.Codes;
import bankdroid.smskey.R;

public class VisitBlogCampaign implements Campaign
{

	@Override
	public View getView( final Context context )
	{
		return View.inflate(context, R.layout.campaign_visitblog, null);
	}

	@Override
	public boolean isActive( final Date lastShown, final int numberOfAppearance, final int hitCount, final int codeCount )
	{
		return hitCount == 0;
	}

	@Override
	public void hit( final Context context )
	{
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Codes.URL_HOME_PAGE)));

	}
}
