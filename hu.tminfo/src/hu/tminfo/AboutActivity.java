package hu.tminfo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
	private final static String BANKDROID_TEXT = "Bankdroid";
	private final static String EMAIL_TEXT = "e-mailben";
	private final static String PORTAL_TEXT = "project oldalon";

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.about);

		findViewById(R.id.marketSearch).setOnClickListener(this);
		findViewById(R.id.url1).setOnClickListener(this);
		findViewById(R.id.url2).setOnClickListener(this);

		//Set links in description0 
		final TextView desc0 = (TextView) findViewById(R.id.description0);
		final String text0 = desc0.getText().toString();
		final SpannableString f0 = new SpannableString(text0);

		final int x0 = text0.indexOf(BANKDROID_TEXT);
		final int y0 = x0 + BANKDROID_TEXT.length();

		f0.setSpan(new ClickSpan(new SpanClickListener()
		{
			@Override
			public void onSpanClicked( final View source, final int spanId )
			{
				trackClickEvent(ACTION_BROWSE, "bankdroid.googlecode.com");

				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/4Ys9pQ")));
			}
		}, 1), x0, y0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		desc0.setText(f0);

		final MovementMethod m0 = desc0.getMovementMethod();
		if ( ( m0 == null ) || !( m0 instanceof LinkMovementMethod ) )
		{
			if ( desc0.getLinksClickable() )
			{
				desc0.setMovementMethod(LinkMovementMethod.getInstance());
			}
		}

		//Set links in description 
		final TextView desc = (TextView) findViewById(R.id.description);
		final String text = desc.getText().toString();
		final SpannableString f = new SpannableString(text);

		int x = text.indexOf(EMAIL_TEXT);
		int y = x + EMAIL_TEXT.length();

		final String bugReportUrl = getString(R.string.aboutBugReportUrl);

		f.setSpan(new ClickSpan(new SpanClickListener()
		{
			@Override
			public void onSpanClicked( final View source, final int spanId )
			{
				trackClickEvent(ACTION_SEND, bugReportUrl);

				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bugReportUrl)));
			}
		}, 1), x, y, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		x = text.indexOf(PORTAL_TEXT);
		y = x + PORTAL_TEXT.length();

		final String issueListUrl = getString(R.string.aboutIssueListUrl);
		f.setSpan(new ClickSpan(new SpanClickListener()
		{
			@Override
			public void onSpanClicked( final View source, final int spanId )
			{
				trackClickEvent(ACTION_BROWSE, issueListUrl);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(issueListUrl)));
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
