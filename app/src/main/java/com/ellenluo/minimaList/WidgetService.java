package com.ellenluo.minimaList;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.w("WidgetService", "Started");
        return (new WidgetListProvider(this.getApplicationContext(), intent));
    }

}
