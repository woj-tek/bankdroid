package sample.twitter;

import java.util.Date;

import android.graphics.drawable.Drawable;

public class TwitterItem
{
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

	public void setId(final String id)
	{
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(final String text)
	{
		this.text = text;
	}

	public Date getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(final Date createdAt)
	{
		this.createdAt = createdAt;
	}

	public void setAvatar(final Drawable avatar)
	{
		this.avatar = avatar;
	}

	public Drawable getAvatar()
	{
		return avatar;
	}
}
