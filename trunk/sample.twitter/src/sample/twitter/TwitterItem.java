package sample.twitter;

import java.util.Date;

public class TwitterItem
{
	private String id;
	private String imageUrl;
	private String text;
	private Date createdAt;

	public TwitterItem(final String id, final String imageUrl, final String text, final Date createdAt)
	{
		super();
		this.id = id;
		this.imageUrl = imageUrl;
		this.text = text;
		this.createdAt = createdAt;
	}

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

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(final String imageUrl)
	{
		this.imageUrl = imageUrl;
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
}
