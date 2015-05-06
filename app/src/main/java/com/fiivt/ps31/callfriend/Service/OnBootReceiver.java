package com.fiivt.ps31.callfriend.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Egor on 06.05.2015.
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceLauncher = new Intent(context, EventService.class);
            context.startService(serviceLauncher);
            Log.v(this.getClass().getName(), "Service loaded while device boot.");
        }
    }
}