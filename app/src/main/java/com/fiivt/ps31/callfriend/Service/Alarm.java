package com.fiivt.ps31.callfriend.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;
import com.fiivt.ps31.callfriend.Utils.EventsGenerator;

import java.util.Date;

/**
 * Created by Egor on 27.05.2015.
 */
public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Put here YOUR code.
        new Thread(new Runnable() {
            Context context;
            @Override
            public void run() {
                EventsGenerator.generate(context);
            }
            public Runnable init(Context context){
                this.context = context;
                return this;
            }
        }.init(context)).start();

        Toast.makeText(context, "Generation of events completed", Toast.LENGTH_LONG).show();

        wl.release();
    }

    public void SetAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 60, pi); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
