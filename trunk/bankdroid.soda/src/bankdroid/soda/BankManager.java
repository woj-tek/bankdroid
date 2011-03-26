package bankdroid.soda;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import bankdroid.soda.bank.Bank;
import bankdroid.soda.bank.BankDescriptor;
import bankdroid.soda.bank.Expression;

/**
 * @author gyenes
 */
public final class BankManager implements Codes
{

	private BankManager()
	{
		// static class. Do not instantiate it...
	}

	private static final char SEPARATOR_CHAR = ',';
	private static final char ESCAPE_CHAR = '"';
	private static final String EMPTY_WORD = "";
	private static final String ESCAPE_CHAR_AS_STRING = String.valueOf(ESCAPE_CHAR);
	private static final String ESCAPE_CHAR_DUPLICATE = ESCAPE_CHAR_AS_STRING + ESCAPE_CHAR_AS_STRING;

	public static String escapeStrings( final String[] strings )
	{
		final StringBuilder builder = new StringBuilder();

		for ( final String string : strings )
		{
			if ( string.indexOf(SEPARATOR_CHAR) >= 0 || string.indexOf(ESCAPE_CHAR) >= 0 )
			{
				final String escaped = string.replace(ESCAPE_CHAR_AS_STRING, ESCAPE_CHAR_DUPLICATE);
				builder.append(ESCAPE_CHAR).append(escaped).append(ESCAPE_CHAR);
			}
			else
			{
				builder.append(string);
			}
			builder.append(SEPARATOR_CHAR);
		}
		if ( builder.length() > 0 )
			builder.delete(builder.length() - 1, builder.length());
		return builder.toString();
	}

	public static String[] unescapeStrings( final String string )
	{
		final int len = string.length();
		if ( len == 0 )
			return new String[0];

		final List<String> result = new ArrayList<String>();
		boolean wordStarted = false;
		int wordStartIndex = -1;
		boolean wordEscaped = false;

		for ( int i = 0; i < len; i++ )
		{
			if ( !wordStarted )
			{

				if ( string.charAt(i) == SEPARATOR_CHAR )
				{
					result.add(EMPTY_WORD);
				}
				else
				{
					wordStarted = true;
					wordEscaped = string.charAt(i) == ESCAPE_CHAR;
					wordStartIndex = i;
					if ( wordEscaped )
					{
						wordStartIndex++;
					}
				}
			}
			// word started
			else if ( string.charAt(i) == ESCAPE_CHAR )
			{
				if ( !wordEscaped )
					throw new IllegalArgumentException("Cannot be quote here as the word was not started with quote: "
							+ i);

				//quote is here
				if ( i + 1 < len )
				{
					if ( string.charAt(i + 1) == ESCAPE_CHAR )
					{
						//double quote: this is a single qoute in the normal text
						i++;
					}
					else if ( string.charAt(i + 1) == SEPARATOR_CHAR )// this is the closing quote
					{
						final String escaped = string.substring(wordStartIndex, i);
						result.add(escaped.replace(ESCAPE_CHAR_DUPLICATE, ESCAPE_CHAR_AS_STRING));
						wordStarted = false;
						wordEscaped = false;
						wordStartIndex = -1;

						i++; // skip the comma
					}
					else
						//invalid character
						throw new IllegalArgumentException("Invalid character at: " + i);

				}
				else
				//last character of the string -> close current word and finish
				{
					final String escaped = string.substring(wordStartIndex, i);
					result.add(escaped.replace(ESCAPE_CHAR_DUPLICATE, ESCAPE_CHAR_AS_STRING));
					wordStarted = false;
					wordEscaped = false;
					wordStartIndex = -1;
				}
			}
			else if ( string.charAt(i) == SEPARATOR_CHAR )
			{
				if ( wordEscaped )
				{
					//comma in the quotes -> go forward, do nothing here
				}
				else
				// end of the world :)
				{
					final String unescaped = string.substring(wordStartIndex, i);
					result.add(unescaped);
					wordStarted = false;
					wordEscaped = false;
					wordStartIndex = -1;
				}
			}
			else
			{
				//do nothing, go forward
			}
		}
		if ( wordStarted && wordEscaped )
			throw new IllegalArgumentException("Unclosed quotes at " + wordStartIndex);

		if ( wordStarted )
		{
			//save last word
			final String unescaped = string.substring(wordStartIndex, len);
			result.add(unescaped);
		}
		else if ( string.charAt(len - 1) == SEPARATOR_CHAR )
		{
			//finish with empty word
			result.add(EMPTY_WORD);
		}

		return result.toArray(new String[result.size()]);
	}

	public static Bank[] findByPhoneNumber( final Context context, final String phoneNumber )
	{
		Bank[] banks = findBank(context, CONTENT_URI,
				Bank.F_PHONENUMBERS + " like " + DatabaseUtils.sqlEscapeString("%" + phoneNumber + "%"), null);

		//match phone number manually: drop out items that has no matching phone number.
		int count = 0;
		int length = banks.length;
		for ( int i = 0; i < length; i++ )
		{
			final String[] numbers = banks[i].getPhoneNumbers();
			boolean match = false;
			for ( final String number : numbers )
			{
				if ( phoneNumber.equals(number) )
				{
					match = true;
					break;
				}
			}
			if ( match )
			{
				count++;
			}
			else
			{
				length--;
				if ( i < length )
				{
					System.arraycopy(banks, i + 1, banks, i, length - i);
				}
				i--;
			}
		}

		if ( length < banks.length )
		{
			final Bank[] temp = new Bank[length];
			System.arraycopy(banks, 0, temp, 0, length);
			banks = temp;
		}
		return banks;
	}

	public static Bank findByUri( final Context context, final Uri uri )
	{
		final Bank[] banks = findBank(context, uri, null, null);
		if ( banks.length > 1 )
			throw new IllegalArgumentException("Too many result.");
		else if ( banks.length == 1 )
			return banks[0];
		else
			return null;
	}

	private static Bank[] findBank( final Context context, final Uri uri, final String selection,
			final String[] selectionArgs )
	{
		final Cursor cursor = context.getContentResolver().query(
				uri,
				new String[] { Bank.F__ID, Bank.F_NAME, Bank.F_VALIDITY, Bank.F_ICON, Bank.F_COUNTRY,
						Bank.F_PHONENUMBERS, Bank.F_EXPRESSIONS }, selection, selectionArgs, Bank.DEFAULT_SORT_ORDER);

		try
		{
			final Bank[] banks = new Bank[cursor.getCount()];
			if ( cursor.moveToFirst() )
			{
				int j = 0;

				do
				{
					final int id = cursor.getInt(0);
					final String name = cursor.getString(1);

					final int expiry = cursor.getInt(2);
					final String icon = cursor.getString(3);
					final String country = cursor.getString(4);
					final String phoneNumbers = cursor.getString(5);
					final String expressions = cursor.getString(6);

					final String[] exps = unescapeStrings(expressions);
					final Expression[] exps2 = new Expression[exps.length];
					for ( int i = 0; i < exps2.length; i++ )
					{
						exps2[i] = new Expression(exps[i]);
					}

					banks[j] = new Bank(id, name, expiry, unescapeStrings(phoneNumbers), exps2, icon, country);
					j++;
				}
				while ( cursor.moveToNext() );
			}
			return banks;
		}
		finally
		{
			if ( cursor != null && !cursor.isClosed() )
				cursor.close();
		}
	}

	public static Bank[] getAllBanks( final Context context )
	{
		final Cursor cursor = context.getContentResolver().query(
				CONTENT_URI,
				new String[] { Bank.F__ID, Bank.F_NAME, Bank.F_VALIDITY, Bank.F_ICON, Bank.F_COUNTRY,
						Bank.F_PHONENUMBERS, Bank.F_EXPRESSIONS }, null, null, Bank.DEFAULT_SORT_ORDER);

		try
		{
			final List<Bank> banks = new ArrayList<Bank>();
			while ( cursor.moveToNext() )
			{
				final int id = cursor.getInt(0);
				final String name = cursor.getString(1);

				Log.d(Codes.TAG, "Bank read: " + id + " - " + name);
				final int expiry = cursor.getInt(2);
				final String icon = cursor.getString(3);
				final String country = cursor.getString(4);
				final String phoneNumbers = cursor.getString(5);
				final String expressions = cursor.getString(6);
				final String[] exps = unescapeStrings(expressions);
				final Expression[] exps2 = new Expression[exps.length];
				for ( int i = 0; i < exps2.length; i++ )
				{
					exps2[i] = new Expression(exps[i]);
				}

				final Bank bank = new Bank(id, name, expiry, unescapeStrings(phoneNumbers), exps2, icon, country);

				banks.add(bank);
			}

			return banks.toArray(new Bank[banks.size()]);
		}
		finally
		{
			if ( cursor != null && !cursor.isClosed() )
				cursor.close();
		}
	}

	/**
	 * Store Bank in the database. Inserts or updates a bank depending on, whether a valid ID is assigned to the 
	 * Bank or not.
	 * @param context Context is used for get reference to the database.
	 * @param b Bank to be inserted or updated.
	 */
	public static void storeBank( final Context context, final Bank b )
	{
		final ContentValues values = new ContentValues();

		values.put(Bank.F_NAME, b.getName());
		values.put(Bank.F_VALIDITY, b.getExpiry());
		values.put(Bank.F_COUNTRY, b.getCountryCode());
		values.put(Bank.F_ICON, b.getIconName());
		final Expression[] exps2 = b.getExtractExpressions();
		final String[] exps = new String[exps2.length];
		for ( int i = 0; i < exps.length; i++ )
		{
			exps[i] = exps2[i].toString(); // use toString, to store the transaction sign flag.
		}
		values.put(Bank.F_EXPRESSIONS, escapeStrings(exps));
		values.put(Bank.F_PHONENUMBERS, escapeStrings(b.getPhoneNumbers()));

		if ( b.getId() == Bank.UNASSIGNED_ID )
		{
			//created as a new bank
			final Uri uri = context.getContentResolver().insert(CONTENT_URI, values);
			b.setId(Integer.parseInt(uri.getPathSegments().get(1)));
			Log.d(TAG, "Bank " + b.getName() + " is inserted with id " + b.getId());
		}
		else
		{
			//update the bank
			final Uri thisUri = CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(b.getId())).build();
			context.getContentResolver().update(thisUri, values, null, null);
			Log.d(TAG, "Bank " + b.getName() + " is updated.");
		}
	}

	public static void updateLastMessage( final Context context, final Message msg )
	{
		if ( msg.bank.getId() == Bank.UNASSIGNED_ID )
			throw new IllegalArgumentException("The bank is not stored in the database yet: " + msg.bank);

		final ContentValues values = new ContentValues();

		values.put(Bank.F_LASTMESSAGE, msg.message);
		values.put(Bank.F_TIMESTAMP, Formatters.getTimstampFormat().format(msg.timestamp));

		//update the bank
		final Uri thisUri = CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(msg.bank.getId())).build();
		context.getContentResolver().update(thisUri, values, null, null);
		Log.d(TAG, "Bank " + msg.bank.getName() + " is updated with last message.");
	}

	/**
	 * Returns the last message (irrespectively to the bank) from the database.
	 * @param context
	 * @return null if no message is received on the phone yet, since the application is installed.
	 */
	public static Message getLastMessage( final Context context )
	{
		Message result = null;
		final Cursor cursor = context.getContentResolver().query(CONTENT_URI,
				new String[] { Bank.F__ID, Bank.F_LASTMESSAGE, Bank.F_TIMESTAMP }, Bank.F_TIMESTAMP + " IS NOT NULL",
				null, Bank.F_TIMESTAMP + " DESC");

		try
		{
			if ( cursor.getCount() > 0 )
			{
				cursor.moveToFirst();
				final int id = cursor.getInt(0);
				final String message = cursor.getString(1);
				Date timestamp;
				try
				{
					timestamp = Formatters.getTimstampFormat().parse(cursor.getString(2));
				}
				catch ( final ParseException e )
				{
					throw new IllegalStateException("Database contains invalid value for timestamp: "
							+ cursor.getString(2), e);
				}
				result = new Message(findByUri(context, Uri.withAppendedPath(CONTENT_URI, String.valueOf(id))),
						message, timestamp);
			}

			return result;
		}
		finally
		{
			if ( cursor != null && !cursor.isClosed() )
				cursor.close();
		}
	}

	private final static Map<String, Drawable> images = new HashMap<String, Drawable>();

	public static Drawable getBankIcon( final Bank bank, final Resources res )
	{
		final String name = bank != null ? bank.getIconName() : null;
		if ( name == null )
		{
			return res.getDrawable(R.drawable.bankdroid_logo);
		}

		if ( images.containsKey(name) )
		{
			Log.d(TAG, "Icon loaded from cache: " + name);
			return images.get(name);
		}

		BitmapDrawable image;

		final DisplayMetrics metrics = res.getDisplayMetrics();

		final Options options = new BitmapFactory.Options();
		options.inDensity = DisplayMetrics.DENSITY_MEDIUM;
		options.inTargetDensity = metrics.densityDpi;
		options.inScaled = false;

		final InputStream is = BankDescriptor.loadLogo(bank);
		if ( is == null )
		{
			return res.getDrawable(R.drawable.bankdroid_logo);
		}

		Log.d(TAG, "Loading image from path: " + name);

		image = new BitmapDrawable(BitmapFactory.decodeStream(is, null, options));

		Log.d(TAG, "Metrics: " + metrics);
		image.setTargetDensity(metrics);
		images.put(name, image);

		Log.d(TAG, "Icon loaded from file: " + name);
		return image;
	}
}
