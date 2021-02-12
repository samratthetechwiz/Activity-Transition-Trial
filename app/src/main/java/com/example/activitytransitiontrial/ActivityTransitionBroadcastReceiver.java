package com.example.activitytransitiontrial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityTransitionBroadcastReceiver extends BroadcastReceiver {

    private final static String TAG = "DETECT TRANSITION";
    private String INTERNAL_RECEIVER_ACTION = "com.package.name.ACTION_INTERNAL_MESSAGE";
    MainActivity mainActivity = new MainActivity();

    public static String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.WALKING:
                return "WALKING";
            default:
                return "UNKNOWN";
        }
    }

    public static String toTransitionType(int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "EXIT";
            default:
                return "UNKNOWN";
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                Log.d(TAG, String.valueOf(event.getActivityType() + " " + event.getTransitionType()));
                String info = toActivityString(event.getActivityType()) +
                        "," + toTransitionType(event.getTransitionType());
                Intent i = new Intent(INTERNAL_RECEIVER_ACTION);

                i.putExtra("Internal Message", info);
                LocalBroadcastManager.getInstance(context).sendBroadcast(i);
            }
        }
    }
}
