package sample.twitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;

public class FeedDownloader extends AsyncTask<String, Integer, List<TwitterItem>>
{
	public final static String TWITTER_API_URL = "http://api.twitter.com/1/statuses/user_timeline.json?screen_name=";

	public final static String JSON_ID = "id_str";
	public final static String JSON_TEXT = "text";
	public final static String JSON_CREATED_AT = "created_at";
	public final static String JSON_USER = "user";
	public final static String JSON_PROFILE_IMAGE_URL = "profile_image_url";

	private Exception e;
	private final TwitterActivity hostActivity;

	public FeedDownloader(final TwitterActivity hostActivity)
	{
		super();

		this.hostActivity = hostActivity;
	}

	@Override
	protected List<TwitterItem> doInBackground(final String... params)
	{
		List<TwitterItem> items = null;
		try
		{
			final URL url = new URL(TWITTER_API_URL + params[0]);
			final InputStream in = (InputStream) url.getContent();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			final StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				buffer.append(line);
			}

			//Thu Apr 21 11:40:55 +0000 2011
			final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");

			//process result
			items = new ArrayList<TwitterItem>();

			final JSONArray jsonItems = new JSONArray(buffer.toString());
			for (int i = 0; i < jsonItems.length(); i++)
			{
				final JSONObject jsonItem = jsonItems.getJSONObject(i);

				final TwitterItem item = new TwitterItem();
				item.setId(jsonItem.getString(JSON_ID));
				item.setText(jsonItem.getString(JSON_TEXT));
				item.setImageUrl(jsonItem.getJSONObject(JSON_USER).getString(JSON_PROFILE_IMAGE_URL));
				item.setCreatedAt(sdf.parse(jsonItem.getString(JSON_CREATED_AT)));

				items.add(item);
			}
		}
		catch (final Exception e)
		{
			this.e = e;
		}
		return items;
	}

	@Override
	protected void onPostExecute(final List<TwitterItem> result)
	{
		if (e != null)
		{
			hostActivity.onLoadFailed(e);
		}
		else
		{
			hostActivity.onLoadSuccesful(result);
		}
	}
}
