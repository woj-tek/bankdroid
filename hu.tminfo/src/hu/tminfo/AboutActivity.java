package hu.tminfo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * TODO different unread indicator for different feed elements
 * TODO English translation see rev 242
 * TODO support for twitter feeds
 * TODO text IDs
 * 
 * TODO review share function
 * TODO do not show new message, if one existing message is updated only
 * TODO if the channel preferences are changed (channel is removed) during offline mode, the db changes won't take effect.
 * 
 * XXX add empty space at the end of the list
 * XXX paging function to be included on item view for easy paging left and right to next items.
 * 
 * 
 * @author gyenes
 */
public class AboutActivity extends TrackedActivity implements Codes, OnClickListener
{
	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.about);

		findViewById(R.id.marketSearch).setOnClickListener(this);
		findViewById(R.id.url1).setOnClickListener(this);
		findViewById(R.id.url2).setOnClickListener(this);

		//format HTML content
		final TextView desc = (TextView) findViewById(R.id.description);
		desc.setText(Html.fromHtml(desc.getText().toString()));
		desc.setMovementMethod(LinkMovementMethod.getInstance());
		final TextView descEN = (TextView) findViewById(R.id.descriptionEN);
		descEN.setText(Html.fromHtml(descEN.getText().toString()));
		descEN.setMovementMethod(LinkMovementMethod.getInstance());

		//set version number
		try
		{
			final PackageManager manager = getPackageManager();
			final PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			final String versionName = info.versionName;
			( (TextView) findViewById(R.id.version) ).setText(versionName);
		}
		catch ( final NameNotFoundException e )
		{
			Log.e(TAG, "Error getting package name.", e);
		}

	}

	@Override
	public void onClick( final View src )
	{
		if ( src.getId() == R.id.marketSearch )
		{
			try
			{
				trackClickEvent(ACTION_BROWSE, "market://search?q=pub:BankDroid");
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:BankDroid")));
			}
			catch ( final Exception e )
			{
				Log.e(TAG, "Nem sikerült megnyitni a piacot.", e);
				final Toast toast = Toast.makeText(getBaseContext(), "Nem sikerült megnyitni a Marketet.",
						Toast.LENGTH_LONG);
				toast.show();
			}
		}
		else if ( src instanceof TextView )
		{
			final String url = ( (TextView) src ).getText().toString();
			trackClickEvent(ACTION_BROWSE, url);
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		}
	}

}
