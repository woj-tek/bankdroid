package hu.androidportal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author gyenes
 */
public class AboutActivity extends Activity implements Codes, OnClickListener
{
	private final static String EMAIL_TEXT = "e-mailben";
	private final static String PORTAL_TEXT = "project oldalakon";

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.about);

		findViewById(R.id.marketSearch).setOnClickListener(this);
		findViewById(R.id.url1).setOnClickListener(this);
		findViewById(R.id.url2).setOnClickListener(this);
		findViewById(R.id.url3).setOnClickListener(this);

		final TextView desc = (TextView) findViewById(R.id.description);
		final String text = desc.getText().toString();
		final SpannableString f = new SpannableString(text);

		int x = text.indexOf(EMAIL_TEXT);
		int y = x + EMAIL_TEXT.length();

		f.setSpan(new ClickSpan(new SpanClickListener()
		{
			@Override
			public void onSpanClicked( final View source, final int spanId )
			{
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:info@bankdroid.info")));
			}
		}, 1), x, y, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		x = text.indexOf(PORTAL_TEXT);
		y = x + PORTAL_TEXT.length();

		f.setSpan(new ClickSpan(new SpanClickListener()
		{
			@Override
			public void onSpanClicked( final View source, final int spanId )
			{
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://code.google.com/p/bankdroid/issues/list")));
			}
		}, 2), x, y, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		desc.setText(f);

		final MovementMethod m = desc.getMovementMethod();
		if ( ( m == null ) || !( m instanceof LinkMovementMethod ) )
		{
			if ( desc.getLinksClickable() )
			{
				desc.setMovementMethod(LinkMovementMethod.getInstance());
			}
		}

	}

	@Override
	public void onClick( final View src )
	{
		if ( src.getId() == R.id.marketSearch )
		{
			try
			{
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
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(( (TextView) src ).getText().toString())));
		}
	}

	static class ClickSpan extends ClickableSpan
	{
		SpanClickListener listener;
		int spanId;

		public ClickSpan( final SpanClickListener listener, final int spanId )
		{
			this.listener = listener;
			this.spanId = spanId;
		}

		@Override
		public void onClick( final View widget )
		{
			listener.onSpanClicked(widget, spanId);
		}
	}

	static interface SpanClickListener
	{
		void onSpanClicked( View source, int spanId );
	}
}
