package bankdroid.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import bankdroid.util.GUIUtil;
import bankdroid.util.TrackedActivity;

public class PropertyViewActivity extends ServiceActivity
{

	private String title;
	private Property[] properties;
	private String shareAnalyticsAction;
	private String shareSubject;
	private String shareBodyTop;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.propertyview);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if ( !SessionManager.getInstance().isLoggedIn() )
			return;

		final Intent intent = getIntent();
		if ( intent != null )
		{
			shareAnalyticsAction = intent.getStringExtra(EXTRA_ANALYTICS_ACTION);
			shareSubject = intent.getStringExtra(EXTRA_SHARE_SUBJECT);
			shareBodyTop = intent.getStringExtra(EXTRA_SHARE_BODY_TOP);
			title = intent.getStringExtra(EXTRA_ACTIVITY_TITLE);
			GUIUtil.setTitle(this, title);

			properties = PropertyHelper.convertArray((Object[]) intent.getSerializableExtra(EXTRA_PROPERTIES));

			final ListView list = (ListView) findViewById(R.id.propertyList);
			list.setAdapter(new PropertyAdapter(properties));
		}
	}

	public void onShareDetails( final View v )
	{
		shareDetails(this, shareAnalyticsAction, shareSubject, shareBodyTop, properties);
	}

	public static void shareDetails( final TrackedActivity caller, final String analyticsAction, final String subject,
			final String bodyTop, final Property[] properties )
	{
		if ( analyticsAction != null )
			caller.trackClickEvent(ACTION_SEND, analyticsAction);

		final Intent send = new Intent(Intent.ACTION_SEND);
		send.putExtra(Intent.EXTRA_SUBJECT, subject);

		final StringBuilder body = new StringBuilder(bodyTop);
		for ( final Property property : properties )
		{
			body.append("\n").append(property.getName()).append(" ").append(property.getValueString());
		}
		body.append("\n\n").append(caller.getString(R.string.shareBodyBottom));
		send.putExtra(Intent.EXTRA_TEXT, body.toString());

		send.setType("text/plain");
		caller.startActivity(Intent.createChooser(send, caller.getString(R.string.shareChooseApplication)));
	}
}
