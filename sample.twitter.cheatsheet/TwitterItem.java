package sample.twitter;

import java.util.Date;

import android.graphics.drawable.Drawable;

public class TwitterItem
{
	public final static String TWITTER_USER_TIMELINE_URL = "http://api.twitter.com/1/statuses/user_timeline.json?screen_name=";
	public final static String TWITTER_PUBLIC_TIMELINE_URL = "http://api.twitter.com/1/statuses/public_timeline.json";

	public final static String JSON_PROFILE_IMAGE_URL = "profile_image_url";
	public final static String JSON_USER = "user";
	public final static String JSON_CREATED_AT = "created_at";
	public final static String JSON_TEXT = "text";
	public final static String JSON_ID = "id_str";

	private String id;
	private Drawable avatar;
	private String text;
	private Date createdAt;

	public TwitterItem()
	{
	}

	public String getId()
	{
		return id;
	}

	public void setId( final String id )
	{
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText( final String text )
	{
		this.text = text;
	}

	public Date getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt( final Date createdAt )
	{
		this.createdAt = createdAt;
	}

	public void setAvatar( final Drawable avatar )
	{
		this.avatar = avatar;
	}

	public Drawable getAvatar()
	{
		return avatar;
	}
}
