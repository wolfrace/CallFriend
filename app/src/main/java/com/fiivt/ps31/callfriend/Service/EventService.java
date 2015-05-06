package com.fiivt.ps31.callfriend.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Egor on 06.05.2015.
 */
public class EventService extends Service {

    final String LOG_TAG = "EventServiceLogs";

    public void onCreate() {
        super.onCreate();
        Log.e(LOG_TAG, "onCreateEventService");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(LOG_TAG, "onStartCommandEventService");
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG, "onDestroyEventService");
    }

    public IBinder onBind(Intent intent) {
        Log.e(LOG_TAG, "onBindEventService");
        return null;
    }

    void someTask() {
    }

}
