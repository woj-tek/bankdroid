import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import sample.twitter.TwitterItem;




----- Downloader:
	List<TwitterItem> items = null;
try
{
	final URL url = new URL(
			params.length > 0 && params[0] != null && params[0].length() > 0 ? TwitterItem.TWITTER_USER_TIMELINE_URL
					+ params[0]
					: TwitterItem.TWITTER_PUBLIC_TIMELINE_URL);
	final InputStream in = (InputStream) url.getContent();
	final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	final StringBuffer buffer = new StringBuffer();
	String line = null;
	while ( ( line = reader.readLine() ) != null )
	{
		buffer.append(line);
	}

	//Thu Apr 21 11:40:55 +0000 2011
	final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");

	//process result
	items = new ArrayList<TwitterItem>();

	final JSONArray jsonItems = new JSONArray(buffer.toString());
	for ( int i = 0; i < jsonItems.length(); i++ )
	{
		final JSONObject jsonItem = jsonItems.getJSONObject(i);

		final TwitterItem item = new TwitterItem();
		item.setId(jsonItem.getString(TwitterItem.JSON_ID));
		item.setText(jsonItem.getString(TwitterItem.JSON_TEXT));
		item.setAvatar(loadImage(jsonItem.getJSONObject(TwitterItem.JSON_USER).getString(
				TwitterItem.JSON_PROFILE_IMAGE_URL)));
		item.setCreatedAt(sdf.parse(jsonItem.getString(TwitterItem.JSON_CREATED_AT)));

		items.add(item);
	}
}
catch ( final Exception e )
{
	this.e = e;
}
return items;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
------------------- Downloader 2
	private Drawable loadImage( final String url )
	{
		if ( icons.containsKey(url) )
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
			catch ( final Exception e )
			{
				Log.e("TW", "Failed to download image: " + url, e);
				Toast.makeText(hostActivity, "Image download failed: " + e, Toast.LENGTH_LONG).show();
				return null;
			}
		}
	}


-- Adapter.getView()
					final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

					if ( convertView == null )
					{
						convertView = View.inflate(TwitterActivity.this, R.layout.feeditem, null);
					}

					final TwitterItem item = getItem(position);
					( (TextView) convertView.findViewById(R.id.text) ).setText(item.getText());
					( (TextView) convertView.findViewById(R.id.createdAt) ).setText(sdf.format(item.getCreatedAt()));
					( (ImageView) convertView.findViewById(R.id.feedIcon) ).setImageDrawable(item.getAvatar());
					return convertView;

