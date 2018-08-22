package com.ellenluo.minimaList;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * WidgetService
 * Created by Ellen Luo
 * RemoteViewsService that calls WidgetListProvider to populate widgets.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetListProvider(this.getApplicationContext(), intent));
    }

}
