package hu.tminfo.widget;

import hu.tminfo.Codes;
import hu.tminfo.ItemListActivity;
import hu.tminfo.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import bankdroid.rss.RSSStream;

/**
 * @author gyenes
 */
public class Widget11Provider extends AppWidgetProvider implements Codes
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
		final ComponentName thisWidget = new ComponentName(context, Widget11Provider.class);
		final int[] widgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		if ( widgetIds == null || widgetIds.length == 0 )
		{
			Log.d(TAG, "There is no widget to update.");
		}

		//get unreadcount from db
		final int unreadCount = RSSStream.getUndreadCount(context);

		//load the last item into all widgets
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget1_1);
		views.setViewVisibility(R.id.badge, unreadCount < 1 ? View.INVISIBLE : View.VISIBLE);

		if ( unreadCount > 0 )
		{
			views.setTextViewText(R.id.badge, unreadCount < 10 ? String.valueOf(unreadCount) : "9+");
		}

		//register onClick listeners
		final Intent listView = new Intent(context, ItemListActivity.class);
		listView.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent intentListView = PendingIntent.getActivity(context, 0, listView, 0);
		views.setOnClickPendingIntent(R.id.widgetBackground, intentListView);
		views.setOnClickPendingIntent(R.id.badge, intentListView);
		views.setOnClickPendingIntent(R.id.widgetTitle, intentListView);

		appWidgetManager.updateAppWidget(thisWidget, views);
	}
}
