package hu.tminfo;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import bankdroid.rss.RSSItem;
import bankdroid.rss.RSSObject;

public class RSSItemProvider extends ContentProvider implements Codes
{

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		private static final String DATABASE_NAME = "bdrss.db";
		private static final int DATABASE_VERSION = 1;//2011-01-08

		DatabaseHelper( final Context context )
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate( final SQLiteDatabase db )
		{
			db.execSQL("CREATE TABLE " + T_RSSITEM + " (" + //
					RSSObject.F__ID + " INTEGER PRIMARY KEY," + // 
					RSSObject.F_DESCRIPTION + " TEXT," + //
					RSSObject.F_SUMMARY + " TEXT," + //
					RSSObject.F_LINK + " TEXT," + //
					RSSObject.F_TITLE + " TEXT," + //
					RSSItem.F_AUTHOR + " TEXT," + //
					RSSItem.F_PUBDATE + " DATE," + //
					RSSItem.F_STATUS + " INTEGER(1)," + //
					RSSItem.F_CHANNELS + " TEXT" + //
					");");
		}

		private void fullCleanUp( final SQLiteDatabase db )
		{
			Log.w(TAG, "Upgrading database that will destroy all old data.");
			db.execSQL("DROP TABLE IF EXISTS " + T_RSSITEM);
			onCreate(db);
		}

		@Override
		public void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion )
		{
			fullCleanUp(db);
		}
	}

	private DatabaseHelper dbHelper;

	private static HashMap<String, String> projectionMap;

	private static final int RSSITEMS = 1;
	private static final int RSSITEM_ID = 2;

	public static final String T_RSSITEM = "T_RSSITEM";

	private static final UriMatcher uriMatcher;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(RSSITEM_PROVIDER_AUTHORITY, "rssitems", RSSITEMS);
		uriMatcher.addURI(RSSITEM_PROVIDER_AUTHORITY, "rssitems/#", RSSITEM_ID);

		projectionMap = new HashMap<String, String>();
		projectionMap.put(RSSObject.F__ID, RSSObject.F__ID);
		projectionMap.put(RSSObject.F_DESCRIPTION, RSSObject.F_DESCRIPTION);
		projectionMap.put(RSSObject.F_SUMMARY, RSSObject.F_SUMMARY);
		projectionMap.put(RSSObject.F_LINK, RSSObject.F_LINK);
		projectionMap.put(RSSObject.F_TITLE, RSSObject.F_TITLE);
		projectionMap.put(RSSItem.F_AUTHOR, RSSItem.F_AUTHOR);
		projectionMap.put(RSSItem.F_PUBDATE, RSSItem.F_PUBDATE);
		projectionMap.put(RSSItem.F_STATUS, RSSItem.F_STATUS);
		projectionMap.put(RSSItem.F_CHANNELS, RSSItem.F_CHANNELS);
	}

	@Override
	public int delete( final Uri uri, final String selection, final String[] selectionArgs )
	{
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = -1;

		final int uriIndex = uriMatcher.match(uri);
		if ( uriIndex == RSSITEMS )
		{
			if ( RSSItem.F_CHANNELS.equals(selection) )
			{
				//delete items based on there channels
				//channel tags are listed as selectionArgs
				for ( final String tag : selectionArgs )
				{
					deleteChannel(db, tag);
				}
			}
			else
			{
				count = db.delete(T_RSSITEM, selection, selectionArgs);
			}
		}
		else if ( uriIndex == RSSITEM_ID )
		{
			final String bankId = uri.getPathSegments().get(1);
			count = db.delete(T_RSSITEM, RSSObject.F__ID + "=" + bankId
					+ ( !TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "" ), selectionArgs);
		}
		else
		{
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	private void deleteChannel( final SQLiteDatabase db, final String tag )
	{
		//2 step deletion: delete items available only on the selection channel; remove tag for multi channel items
		final int count1 = db.delete(T_RSSITEM, RSSItem.F_CHANNELS + "=' " + tag + " '", null);

		//select remaining items
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(T_RSSITEM);
		qb.setProjectionMap(projectionMap);

		// Get the database and run the query
		final Cursor c = qb.query(db, new String[] { RSSItem.F__ID, RSSItem.F_CHANNELS }, RSSItem.F_CHANNELS
				+ " like '% " + tag + " %'", null, null, null, null);
		final int count2 = c.getCount();

		//iterate through and remove channel tags one-by-one
		while ( c.moveToNext() )
		{
			final long id = c.getLong(0);
			final String currentChannels = c.getString(1);

			final String newChannels = RSSItem.removeChannelFromList(currentChannels, tag);
			final ContentValues values = new ContentValues(1);
			values.put(RSSItem.F_CHANNELS, newChannels);
			db.update(T_RSSITEM, values, RSSItem.F__ID + "=" + id, null);

		}

		c.close();

		Log.d(TAG, "Channel removed with " + count1 + " items and " + count2 + " tags.");
	}

	@Override
	public String getType( final Uri uri )
	{
		switch ( uriMatcher.match(uri) )
		{
		case RSSITEMS:
			return RSSItem.CONTENT_TYPE;

		case RSSITEM_ID:
			return RSSItem.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert( final Uri uri, final ContentValues values )
	{
		// Validate the requested uri
		if ( uriMatcher.match(uri) != RSSITEMS )
		{
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		//Make sure that the mandatory fields are all set
		if ( !values.containsKey(RSSObject.F_DESCRIPTION) || !values.containsKey(RSSObject.F_LINK)
				|| !values.containsKey(RSSObject.F_TITLE) || !values.containsKey(RSSItem.F_AUTHOR) )
		{
			throw new IllegalArgumentException("New bank record is not complete. Missing values!");
		}

		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		final long rowId = db.insert(T_RSSITEM, "rssitem", values);

		if ( rowId > 0 )
		{
			final Uri rssUri = ContentUris.withAppendedId(RSSItem.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(rssUri, null);
			return rssUri;
		}
		else
		{
			//handle duplication here: manage the channel flags
			final long id = values.getAsLong(RSSItem.F__ID);
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(T_RSSITEM);

			final Cursor c = qb.query(db, new String[] { RSSItem.F_CHANNELS }, RSSItem.F__ID + "=" + id, null, null,
					null, null);

			try
			{
				if ( c.moveToFirst() )
				{
					final Uri rssUri = ContentUris.withAppendedId(RSSItem.CONTENT_URI, id);

					//check channel changes
					final String currentChannels = c.getString(0);
					final String newChannels = values.getAsString(RSSItem.F_CHANNELS);
					if ( !RSSItem.isChannelContained(currentChannels, newChannels) )
					{
						final String mergedList = RSSItem.mergeChannels(currentChannels, newChannels);
						//Log.d(TAG, "Channel list merged: " + mergedList);

						//update here
						final ContentValues updateValues = new ContentValues();
						updateValues.put(RSSItem.F_CHANNELS, mergedList);
						final int updatedCount = db.update(T_RSSITEM, updateValues, RSSItem.F__ID + "=" + id, null);
						if ( updatedCount > 0 )
						{
							getContext().getContentResolver().notifyChange(rssUri, null);
						}
					}
					else
					{
						//Log.d(TAG, "There is nothing to update: " + currentChannels + "=" + newChannels);
					}//FIXME try excetpion
					return rssUri;
				}
			}
			finally
			{
				c.close();
			}

			//if failed to insert, but there is nothing to update, the operation should fail.
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate()
	{
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query( final Uri uri, final String[] projection, final String selection,
			final String[] selectionArgs, final String sortOrder )
	{
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch ( uriMatcher.match(uri) )
		{
		case RSSITEMS:
			qb.setTables(T_RSSITEM);
			qb.setProjectionMap(projectionMap);
			break;

		case RSSITEM_ID:
			qb.setTables(T_RSSITEM);
			qb.setProjectionMap(projectionMap);
			qb.appendWhere(RSSObject.F__ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if ( TextUtils.isEmpty(sortOrder) )
		{
			orderBy = RSSItem.DEFAULT_SORT_ORDER;
		}
		else
		{
			orderBy = sortOrder;
		}

		// Get the database and run the query
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		final Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update( final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs )
	{
		final SQLiteDatabase db = dbHelper.getWritableDatabase();

		int count;
		switch ( uriMatcher.match(uri) )
		{
		case RSSITEMS:
			count = db.update(T_RSSITEM, values, selection, selectionArgs);
			break;

		case RSSITEM_ID:
			final String bankId = uri.getPathSegments().get(1);
			count = db.update(T_RSSITEM, values, RSSObject.F__ID + "=" + bankId
					+ ( !TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "" ), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
