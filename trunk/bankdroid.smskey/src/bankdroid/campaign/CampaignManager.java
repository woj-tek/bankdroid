package bankdroid.campaign;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import bankdroid.smskey.Codes;

public class CampaignManager
{
	public final static List<Campaign> campaigns = new ArrayList<Campaign>();
	static
	{
		campaigns.add(new MarketRateCampaign());
	}

	private final Context context;
	private final SharedPreferences preferences;

	public CampaignManager( final Context context )
	{
		super();
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void show( final RelativeLayout parent )
	{
		final Campaign campaign = getActiveCampaign();

		final View view = campaign.getView(context);

		//calculate 50dp instead of 50px
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
		final int pixelSize = (int) ( 50f * dm.density );

		final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, pixelSize);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

		parent.addView(view, params);

		//FIXME add animation and delay
	}

	private Campaign getActiveCampaign()
	{
		final int codeCount = preferences.getInt(Codes.PREF_CODE_COUNT, 0);
		for ( final Campaign campaign : campaigns )
		{
			final CampaignStatus status = loadCampaignStatus(campaign);

			if ( campaign.isActive(status.getLastShown(), status.getNumberOfShow(), status.getHitCount(), codeCount) )
			{
				status.setNumberOfShow(status.getNumberOfShow() + 1);
				status.setLastShown(new Date());
				saveCampaignStatus(campaign, status);

				return campaign;
			}
		}
		return null;
	}

	private void saveCampaignStatus( final Campaign campaign, final CampaignStatus status )
	{
		final String statusString = status.toString();
		final String key = campaign.getClass().getName();
		final Editor editor = preferences.edit();
		editor.putString(key, statusString);
		editor.commit();
	}

	private CampaignStatus loadCampaignStatus( final Campaign campaign )
	{
		final String key = campaign.getClass().getName();

		String statusString = null;
		if ( preferences.contains(key) )
		{
			statusString = preferences.getString(key, null);
		}

		CampaignStatus status = null;
		if ( statusString != null )
		{
			status = CampaignStatus.fromString(statusString);
		}
		else
		{
			status = new CampaignStatus();
		}

		return status;
	}
}
