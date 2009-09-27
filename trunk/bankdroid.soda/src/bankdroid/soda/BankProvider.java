package bankdroid.soda;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class BankProvider extends ContentProvider implements Codes
{

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		private static final String DATABASE_NAME = "bank.db";
		private static final int DATABASE_VERSION = 1;

		DatabaseHelper( final Context context )
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate( final SQLiteDatabase db )
		{
			db.execSQL("CREATE TABLE " + T_BANK + " (" + //
					F__ID + " INTEGER PRIMARY KEY," + // 
					F_ID + " TEXT," + //
					F_VALIDITY + " INTEGER," + //
					F_ICON + " INTEGER," + //
					");");

			//load constants here
			final SQLiteStatement stmt = db.compileStatement("INSERT INTO " + T_BANK + " VALUES (?,?,?,?)");
			final Bank[] banks = Bank.getDefaultBanks();
			for ( int i = 0; i < banks.length; i++ )
			{
				final Bank bank = banks[i];
				stmt.clearBindings();
				stmt.bindLong(0, i);
				stmt.bindString(1, bank.getName());
				stmt.bindLong(2, bank.getExpiry());
				stmt.bindLong(3, bank.getIconId());

				stmt.execute();
			}
		}

		@Override
		public void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion )
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + T_BANK);
			onCreate(db);
		}
	}

	private DatabaseHelper dbHelper;

	private static HashMap<String, String> projectionMap;

	private static final int BANKS = 1;
	private static final int BANK_ID = 2;

	private static final String T_BANK = "T_BANK";

	private static final UriMatcher uriMatcher;

	private static final String DEFAULT_SORT_ORDER = F_ID;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_AUTHORITY, "banks", BANKS);
		uriMatcher.addURI(PROVIDER_AUTHORITY, "banks/#", BANK_ID);

		projectionMap = new HashMap<String, String>();
		projectionMap.put(F__ID, F__ID);
		projectionMap.put(F_ID, F_ID);
		projectionMap.put(F_VALIDITY, F_VALIDITY);
		projectionMap.put(F_ICON, F_ICON);
	}

	@Override
	public int delete( final Uri arg0, final String arg1, final String[] arg2 )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType( final Uri uri )
	{
		switch ( uriMatcher.match(uri) )
		{
		case BANKS:
			return Bank.CONTENT_TYPE;

		case BANK_ID:
			return Bank.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert( final Uri uri, final ContentValues values )
	{
		// TODO Auto-generated method stub
		return null;
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
		case BANKS:
			qb.setTables(T_BANK);
			qb.setProjectionMap(projectionMap);
			break;

		case BANK_ID:
			qb.setTables(T_BANK);
			qb.setProjectionMap(projectionMap);
			qb.appendWhere(F__ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if ( TextUtils.isEmpty(sortOrder) )
		{
			orderBy = DEFAULT_SORT_ORDER;
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
		// TODO Auto-generated method stub
		return 0;
	}

}
