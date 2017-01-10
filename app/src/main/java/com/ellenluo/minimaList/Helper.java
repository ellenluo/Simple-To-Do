package com.ellenluo.minimaList;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

class Helper {

    Context context;

    Helper(Context context) {
        this.context = context;
    }

    // find height of status bar
    int getStatusBarHeight() {
        int result = 0;
        int resourceId = this.context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // displays alert confirmation message
    void displayAlert(String message, String posMsg, String negMsg) {
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

    void updateWidgets() {
        // update widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this.context, WidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.task_list);
        Log.w("Reference", "widgets updated");
    }

    void setTheme() {
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(this.context);
        int color = prefSettings.getInt("theme_color", 4149685);

        if (color == ContextCompat.getColor(this.context, R.color.red)) {
            this.context.setTheme(R.style.RedTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.pink)) {
            this.context.setTheme(R.style.PinkTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.purple)) {
            this.context.setTheme(R.style.PurpleTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.deep_purple)) {
            this.context.setTheme(R.style.DeepPurpleTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.dark_blue)) {
            this.context.setTheme(R.style.AppTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.light_blue)) {
            this.context.setTheme(R.style.LightBlueTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.aqua)) {
            this.context.setTheme(R.style.AquaTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.green)) {
            this.context.setTheme(R.style.GreenTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.orange)) {
            this.context.setTheme(R.style.OrangeTheme);
        } else {
            this.context.setTheme(R.style.BlueGreyTheme);
        }
    }

}
