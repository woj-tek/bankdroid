package bankdroid.start;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import bankdroid.util.GUIUtil;

public class PropertyViewActivity extends ServiceActivity
{

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
			final String title = intent.getStringExtra(EXTRA_ACTIVITY_TITLE);
			GUIUtil.setTitle(this, title);

			final Property[] properties = PropertyHelper.convertArray((Object[]) intent
					.getSerializableExtra(EXTRA_PROPERTIES));

			final ListView list = (ListView) findViewById(R.id.propertyList);
			list.setAdapter(new PropertyAdapter(properties));
		}
	}
}
