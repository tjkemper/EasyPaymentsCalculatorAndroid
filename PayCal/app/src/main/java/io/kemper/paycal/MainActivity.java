package io.kemper.paycal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import io.kemper.paycal.app.PayCalApplication;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the shared Tracker instance.
        PayCalApplication application = (PayCalApplication) getApplication();
        mTracker = application.getDefaultTracker();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Setting screen name: " + "activity_main");
        mTracker.setScreenName("Image~" + "activity_main");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void onClickTryIt(View view) {

        Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
        startActivity(intent);

    }
}
