package sample.twitter;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterActivity extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onLoadFailed(final Exception e)
	{
		Log.e("TF", "Load failed. ", e);
		Toast.makeText(this, "Download failed: " + e, Toast.LENGTH_LONG).show();
	}

	public void onLoadSuccesful(final List<TwitterItem> items)
	{
		Log.d("TF", "Download was succesful.");
		if (items == null || items.size() < 1)
		{
			Toast.makeText(this, "Empty user feed.", Toast.LENGTH_SHORT);
		}
		else
		{
			final ArrayAdapter<TwitterItem> adapter = new ArrayAdapter<TwitterItem>(this, R.layout.feeditem, items) {
				@Override
				public View getView(final int position, View convertView, final ViewGroup parent)
				{
					final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

					if (convertView == null)
					{
						convertView = View.inflate(TwitterActivity.this, R.layout.feeditem, null);
					}

					final TwitterItem item = getItem(position);
					((TextView) convertView.findViewById(R.id.text)).setText(item.getText());
					((TextView) convertView.findViewById(R.id.createdAt)).setText(sdf.format(item.getCreatedAt()));
					((ImageView) convertView.findViewById(R.id.feedIcon))
							.setImageDrawable(loadImage(item.getImageUrl()));
					return convertView;
				}
			};

			((ListView) findViewById(R.id.feedList)).setAdapter(adapter);
		}
	}

	private final Map<String, Drawable> icons = new HashMap<String, Drawable>();

	private Drawable loadImage(final String url)
	{
		if (icons.containsKey(url))
		{
			return icons.get(url);
		}
		else
		{
			try
			{
				final URL urlUrl = new URL(url);
				final InputStream content = (InputStream) urlUrl.getContent();
				final Drawable d = Drawable.createFromStream(content, "src");
				icons.put(url, d);
				return d;
			}
			catch (final Exception e)
			{
				Log.e("TW", "Failed to download image: " + url, e);
				Toast.makeText(this, "A kép letöltése sikertelen volt: " + e, Toast.LENGTH_LONG).show();
				return null;
			}
		}
	}

	public void onLoadFeed(final View v)
	{
		final String userName = ((EditText) findViewById(R.id.userName)).getText().toString();
		final FeedDownloader downloader = new FeedDownloader(this);
		downloader.execute(userName);
	}
}