package hu.androidportal.widget;

import hu.androidportal.Codes;
import hu.androidportal.ItemListActivity;
import hu.androidportal.ItemViewActivity;
import hu.androidportal.R;
import hu.androidportal.rss.RSSItem;
import hu.androidportal.rss.RSSStream;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

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
		//get last item from DB
		final RSSItem last = RSSStream.getLast(context);

		//load the last item into all widgets
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		views.setTextViewText(R.id.title, last.title);
		views.setTextViewText(R.id.summary, last.summary);
		views.setTextViewText(R.id.author, ItemListActivity.getAuthorText(last.author, last.publishDate));

		//register onClick listeners
		final Intent itemView = new Intent(context, ItemViewActivity.class);
		itemView.setAction(Intent.ACTION_VIEW);
		itemView.setData(Uri.withAppendedPath(RSSItem.CONTENT_URI, String.valueOf(last.id)));
		itemView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent intentItemView = PendingIntent.getActivity(context, 0, itemView, 0);

		views.setOnClickPendingIntent(R.id.widgetBackground, intentItemView);
		views.setOnClickPendingIntent(R.id.title, intentItemView);
		views.setOnClickPendingIntent(R.id.summary, intentItemView);
		views.setOnClickPendingIntent(R.id.author, intentItemView);

		final Intent listView = new Intent(context, ItemListActivity.class);
		listView.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		final PendingIntent intentListView = PendingIntent.getActivity(context, 0, listView, 0);
		views.setOnClickPendingIntent(R.id.droidLogo, intentListView);

		final ComponentName thisWidget = new ComponentName(context, PortalWidgetProvider.class);
		appWidgetManager.updateAppWidget(thisWidget, views);
		Log.d("APHUw", "Widget is updated.");
	}
}
