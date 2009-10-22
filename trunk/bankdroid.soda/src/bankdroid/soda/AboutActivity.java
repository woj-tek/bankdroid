package bankdroid.soda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener, Codes
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		( (Button) findViewById(R.id.projectHomeButton) ).setOnClickListener(this);
		( (Button) findViewById(R.id.productInfoButton) ).setOnClickListener(this);
		( (TextView) findViewById(R.id.version) ).setText(VERSION);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.productInfoButton )
		{
			final Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(INFO_SITE_URL));
			startActivity(viewIntent);
		}
		else if ( v.getId() == R.id.projectHomeButton )
		{
			final Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(PROJECT_HOME_URL));
			startActivity(viewIntent);
		}
	}

}
