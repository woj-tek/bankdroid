package hu.tminfo.widget;

import hu.tminfo.Codes;
import hu.tminfo.ItemListActivity;
import hu.tminfo.ItemViewActivity;
import hu.tminfo.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import bankdroid.rss.RSSItem;
import bankdroid.rss.RSSStream;

/**
 * @author gyenes
 */
public class PortalWidgetProvider extends AppWidgetProvider implements Codes
{

	@Override
	public void onUpdate( final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds )
	{
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		updateWidgets(context, appWidgetManager);
	}

	public static void updateWidgets( final Context context, final AppWidgetManager appWidgetManager )
	{
		//check whether there is any widget installed
		final ComponentName thisWidget = new ComponentName(context, PortalWidgetProvider.class);
		final int[] widgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		if ( widgetIds == null || widgetIds.length == 0 )
		{
			Log.d(TAG, "There is no widget to update.");
		}

		//get last item from DB
		final RSSItem last = RSSStream.getLast(context);

		//last can be null if the db is empty
		if ( last == null )
			return;

		//load the last item into all widgets
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		views.setTextViewText(R.id.title, last.title);
		views.setTextViewText(R.id.summary, last.summary);
		views.setTextViewText(R.id.author, ItemListActivity.getAuthorText(last.author, last.publishDate));

		final int unreadCount = RSSStream.getUndreadCount(context);
		views.setViewVisibility(R.id.badge, unreadCount < 1 ? View.INVISIBLE : View.VISIBLE);

		if ( unreadCount > 0 )
		{
			views.setTextViewText(R.id.badge, unreadCount < 10 ? String.valueOf(unreadCount) : "9+");
		}

		//register onClick listeners
		final Intent itemView = new Intent(context, ItemViewActivity.class);
		itemView.setAction(Intent.ACTION_VIEW);
		itemView.setData(Uri.withAppendedPath(RSSItem.CONTENT_URI, String.valueOf(last.id)));
		itemView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent intentItemView = PendingIntent.getActivity(context, 0, itemView, 0);

		views.setOnClickPendingIntent(R.id.title, intentItemView);
		views.setOnClickPendingIntent(R.id.summary, intentItemView);
		views.setOnClickPendingIntent(R.id.author, intentItemView);

		final Intent listView = new Intent(context, ItemListActivity.class);
		listView.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent intentListView = PendingIntent.getActivity(context, 0, listView, 0);
		views.setOnClickPendingIntent(R.id.droidLogo, intentListView);
		views.setOnClickPendingIntent(R.id.readIndicator, intentListView);
		views.setOnClickPendingIntent(R.id.widgetBackground, intentListView);

		appWidgetManager.updateAppWidget(thisWidget, views);
	}
}
