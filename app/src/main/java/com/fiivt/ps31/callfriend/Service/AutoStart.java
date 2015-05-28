package com.fiivt.ps31.callfriend.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Egor on 27.05.2015.
 */
public class AutoStart extends BroadcastReceiver {
    Alarm alarm = new Alarm();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            alarm.SetAlarm(context);
        }
    }
}
