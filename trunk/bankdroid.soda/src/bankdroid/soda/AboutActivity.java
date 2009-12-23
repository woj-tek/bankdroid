package bankdroid.soda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author gyenes
 *
 * FIXME add scrollbar to the view
 * FIXME add comments for reporting bugs and sending samples
 * FIXME linkify texts if possible.
 */
public class AboutActivity extends Activity implements OnClickListener, Codes
{

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		( (Button) findViewById(R.id.productInfoButton) ).setOnClickListener(this);
		( (TextView) findViewById(R.id.version) ).setText(VERSION);
	}

	@Override
	public void onClick( final View v )
	{
		if ( v.getId() == R.id.productInfoButton )
		{
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_INFO_SITE));
			startActivity(viewIntent);
		}
	}

}
