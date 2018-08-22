package com.ellenluo.minimaList;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

/**
 * WidgetProvider
 * Created by Ellen Luo
 * AppWidgetProvider that creates and updates widgets.
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final int PREFERENCE_MODE_PRIVATE = 0;
    private static final String ACTION_SCHEDULED_UPDATE = "scheduled_update";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ACTION_SCHEDULED_UPDATE)) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
            onUpdate(context, manager, ids);
        }
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // set up widget service
            Intent service = new Intent(context, WidgetService.class);
            service.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            service.setData(Uri.parse(service.toUri(Intent.URI_INTENT_SCHEME)));

            // set up widget view
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setRemoteAdapter(R.id.task_list, service);
            remoteViews.setEmptyView(R.id.task_list, R.id.empty_list);

            // set header bar color
            SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(context);
            int color = prefSettings.getInt("theme_color", ContextCompat.getColor(context, R.color.dark_blue));
            remoteViews.setInt(R.id.widget_title, "setBackgroundColor", color);

            // set widget title
            SharedPreferences pref = context.getSharedPreferences(String.valueOf(appWidgetId), PREFERENCE_MODE_PRIVATE);
            String list = pref.getString("widget_list", "All Tasks");
            remoteViews.setTextViewText(R.id.widget_title, list);

            // set list item click event
            Intent listIntent = new Intent(context, TaskDetailsActivity.class);
            PendingIntent listPendingIntent = PendingIntent.getActivity(context, 0, listIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.task_list, listPendingIntent);

            // set header click event
            Intent headerIntent = new Intent(context, MainActivity.class);
            headerIntent.putExtra("current_list", list);
            PendingIntent headerPendingIntent = PendingIntent.getActivity(context, 0, headerIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_title, headerPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        scheduleNextUpdate(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // schedules update at midnight
    private void scheduleNextUpdate(Context context) {
        Helper h = new Helper(context);
        h.scheduleUpdate();
    }

}
