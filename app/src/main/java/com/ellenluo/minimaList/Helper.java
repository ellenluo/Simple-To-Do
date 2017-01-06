package com.ellenluo.minimaList;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class Helper {

    Context context;

    public Helper(Context context) {
        this.context = context;
    }

    // find height of status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = this.context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // displays alert confirmation message
    public void displayAlert(String message, String posMsg, String negMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage(message);

        builder.setNegativeButton(negMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        });

        builder.setPositiveButton(posMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateWidgets() {
        // update widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this.context, WidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.task_list);
        Log.w("Reference", "widgets updated");
    }

}
