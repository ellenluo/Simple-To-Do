package com.ellenluo.minimaList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

/**
 * IntroActivity
 * Created by Ellen Luo
 * Activity that displays an image with basic instructions.
 */

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Google analytics
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);

        setContentView(R.layout.activity_intro);
    }

    // done button pressed
    public void doneIntro(View view) {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        IntroActivity.this.finish();
    }

}
