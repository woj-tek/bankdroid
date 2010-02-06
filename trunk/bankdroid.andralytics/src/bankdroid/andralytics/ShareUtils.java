package bankdroid.andralytics;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

public class ShareUtils implements OnClickListener
{
	public final static int TWITTER = 1;
	public final static int FACEBOOK = 2;
	public final static int MYSPACE = 3;
	public final static int EMAIL = 4;

	private final int channel;
	private final String url;
	private final String subject;
	private final String text;

	private ShareUtils( final int channel, final String url, final String text, final String subject )
	{
		this.channel = channel;
		this.url = url;
		this.text = text;
		this.subject = text;
	}

	@Override
	public void onClick( final View v )
	{
		if ( channel == TWITTER )
		{
			final Intent send = new Intent(Intent.ACTION_SEND);

			send.putExtra(Intent.EXTRA_TEXT, text + " " + url);
			send.setType("text/plain");

			v.getContext().startActivity(Intent.createChooser(send, "Válassz alkalmazást:"));
		}
		else if ( channel == FACEBOOK )
		{
			//http://www.facebook.com/sharer.php?u=http%3A%2F%2Fbit.ly%2FcOOLc0&t=Join%20to%20Android%20community%20statistics...
			final StringBuilder uri = new StringBuilder("http://www.facebook.com/sharer.php?u=");
			uri.append(Uri.encode(url));
			uri.append("&t=");
			uri.append(Uri.encode(text));

			v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString())));
		}
		else if ( channel == MYSPACE )
		{
			//http://www.myspace.com/index.cfm?fuseaction=postto&t=Join%20to%20Android%20community%20statistics...&c=&u=http%3A%2F%2Fbit.ly%2FcOOLc0&l=
			final StringBuilder uri = new StringBuilder("http://www.myspace.com/index.cfm?fuseaction=postto&t=");
			uri.append(Uri.encode(text));
			uri.append("&c=&u=");
			uri.append(Uri.encode(url));
			uri.append("&l=");

			v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString())));
		}
		else if ( channel == EMAIL )
		{
			final Intent send = new Intent(Intent.ACTION_SEND);

			send.putExtra(Intent.EXTRA_SUBJECT, subject);
			send.putExtra(Intent.EXTRA_TEXT, text);
			send.setType("message/rfc822");

			v.getContext().startActivity(Intent.createChooser(send, "Válassz alkalmazást:"));
		}
	}

	public static void shareOnTwitter( final View v, final String url, final String text )
	{
		v.setOnClickListener(new ShareUtils(TWITTER, url, text, null));
	}

	public static void shareOnFacebook( final View v, final String url, final String text )
	{
		v.setOnClickListener(new ShareUtils(FACEBOOK, url, text, null));
	}

	public static void shareOnMySpace( final View v, final String url, final String text )
	{
		v.setOnClickListener(new ShareUtils(MYSPACE, url, text, null));
	}

	public static void shareInMail( final View v, final String text, final String subject )
	{
		v.setOnClickListener(new ShareUtils(EMAIL, null, text, subject));
	}
}
