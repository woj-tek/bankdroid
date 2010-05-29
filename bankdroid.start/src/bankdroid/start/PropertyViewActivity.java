package bankdroid.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.csaba.connector.model.AbstractRemoteObject;

public class PropertyViewActivity extends ServiceActivity
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

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
			( (TextView) findViewById(R.id.title) ).setText(title);

			final AbstractRemoteObject object = (AbstractRemoteObject) intent
					.getSerializableExtra(EXTRA_PROPERTY_OBJECT);
			final String[] defaultLabels = intent.getStringArrayExtra(EXTRA_PROPERTY_DEFAULT_LABELS);
			final String[] defaultValues = intent.getStringArrayExtra(EXTRA_PROPERTY_DEFAULT_VALUES);

			final ListView list = (ListView) findViewById(R.id.propertyList);
			list.setAdapter(new PropertyAdapter(object, defaultLabels, defaultValues));

			//FIXME handle defaults
		}
	}
}
