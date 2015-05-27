package com.fiivt.ps31.callfriend.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Egor on 27.05.2015.
 */
public class EventServiceStartUpReciever extends BroadcastReceiver {

    static public final String Action_A = "com.fiivt.ps31.callfriend.Service.MY_STARTUP_SERVICE";
    static public final long cooldown = 30000;

    @Override
    public void onReceive(Context context, Intent intent){
        // ��������� ��� ������
        Intent iStartService = new Intent(context, EventService.class);
        context.startService(iStartService);

        // ��������� ��������� ��������� �� �������
        // ���� ���������� ����, ��� ���� ���� ��� �� �����
        // ��� ����� ������� ������ ����� � 1.5 ������
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, EventServiceStartUpReciever.class);

        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
        long t = System.currentTimeMillis() + cooldown*3;
        long time_update = cooldown*3;
        //alarmManager.setRepeating(AlarmManager.RTC, t, time_update, pending);

        // ���� �������� ������������� time_update �� ��� �����,
        // �� ����������� ����������� ������� setInexactRepeating
        alarmManager.setInexactRepeating(AlarmManager.RTC, t, time_update, pending);
    }

}
