package com.ellenluo.minimaList;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int i = 0; i < appWidgetIds.length; i++){
            Log.w("WidgetProvider", "i = " + i + ", appWidgetIds length = " + appWidgetIds.length);
            Intent service = new Intent(context, WidgetService.class);
            service.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            service.setData(Uri.parse(service.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setRemoteAdapter(R.id.task_list, service);
            remoteViews.setEmptyView(R.id.task_list, R.id.empty_list);
            remoteViews.setTextColor(R.id.empty_list, Color.parseColor("#757575"));

            SharedPreferences pref = context.getSharedPreferences("Main", 0);
            String list = pref.getString("widget_list", "All Tasks");
            remoteViews.setTextViewText(R.id.widget_title, list);

            Intent intent = new Intent(context, TaskDetailsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.task_list, pendingIntent);

            //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.task_list);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
