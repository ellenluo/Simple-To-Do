package com.ellenluo.minimaList;

/**
 * AnalyticsApplication
 * Created by Ellen Luo and Google Analytics
 * Application that allows Google Analytics tracking
 */

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsApplication extends Application {
    private Tracker mTracker;

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.analytics);
            analytics.enableAutoActivityReports(this);
        }
        return mTracker;
    }
}
