package sample.twitter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
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

	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onLoadFailed(final Exception e)
	{
		dialog.dismiss();
		Toast.makeText(this, "Download failed: " + e, Toast.LENGTH_LONG).show();
	}

	public void onLoadSuccesful(final List<TwitterItem> items)
	{
		dialog.dismiss();
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
					((ImageView) convertView.findViewById(R.id.feedIcon)).setImageDrawable(item.getAvatar());
					return convertView;
				}
			};

			((ListView) findViewById(R.id.feedList)).setAdapter(adapter);
		}
	}

	public void onLoadFeed(final View v)
	{
		final String userName = ((EditText) findViewById(R.id.userName)).getText().toString();
		final FeedDownloader downloader = new FeedDownloader(this);
		downloader.execute(userName);

		dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
		dialog.show();
	}
}