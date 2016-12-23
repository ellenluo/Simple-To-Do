package com.ellenluo.minimaList;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    public static String UPDATE_LIST = "UPDATE_LIST";
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent service = new Intent(context, WidgetService.class);
            service.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            service.setData(Uri.parse(service.toUri(Intent.URI_INTENT_SCHEME)));

            // set up widget view
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setRemoteAdapter(R.id.task_list, service);
            remoteViews.setEmptyView(R.id.task_list, R.id.empty_list);

            // set widget title
            SharedPreferences pref = context.getSharedPreferences(String.valueOf(appWidgetId), PREFERENCE_MODE_PRIVATE);
            String list = pref.getString("widget_list", "All Tasks");
            Log.w("WidgetProvider", "appWidgetId is " + appWidgetId + ", list name is " + list);
            remoteViews.setTextViewText(R.id.widget_title, list);

            // set list item click event
            Intent listIntent = new Intent(context, TaskDetailsActivity.class);
            PendingIntent listPendingIntent = PendingIntent.getActivity(context, 0, listIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.task_list, listPendingIntent);

            // set header click event
            Intent headerIntent = new Intent(context, MainActivity.class);
            headerIntent.putExtra("current_list", list);
            PendingIntent headerPendingIntent = PendingIntent.getActivity(context, 0, headerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_title, headerPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);

        if (UPDATE_LIST.equals(intent.getAction())) {
            updateTitle(context);
        }
    }

    public void updateTitle(Context context) {
        Log.w("WidgetProvider", "updateTitle called");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));

        if (appWidgetIds != null && appWidgetIds.length > 0) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setTextViewText(R.id.widget_title, "All Tasks");

            Intent clickIntent = new Intent(context, TaskDetailsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.task_list, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }
    }

}
