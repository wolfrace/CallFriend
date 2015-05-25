package com.fiivt.ps31.callfriend.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate;
import com.fiivt.ps31.callfriend.Utils.Status;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Egor on 06.05.2015.
 */
public class EventService extends Service {

    final String LOG_TAG = "EventServiceLogs";
    private Date lastDateGenerate;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
               // if (lastDateGenerate != null && TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastDateGenerate.getTime()) > 1) {
                    lastDateGenerate = new Date();
                    generateEvents();
             //   }
                //temp
                stopSelf();
            }
        }).start();
    }

    private final static int generateForXdays = 30;
    public void generateEvents() {
        Log.e(LOG_TAG, "generateEventsFromEventService");

        AppDb appDb = new AppDb(this);
        List<PersonTemplate> personTemplates = appDb.getEnabledPersonTemplates(Integer.MAX_VALUE, 0);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        GregorianCalendar today = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));
        for (int i = 0; i < personTemplates.size(); ++i) {
            Event lastEvent = appDb.getLastEventByPersonTemplate(personTemplates.get(i).getId());
            if (lastEvent == null)
            {//todo ref
                Date eventDate = personTemplates.get(i).getCustomDate();
                while (eventDate.getTime() < today.getTime().getTime())
                {
                    eventDate = personTemplates.get(i).applyCooldown(eventDate);//.setTime(eventDate.getTime() + personTemplates.get(i).getCooldown().getTime());
                }
                lastEvent = new Event(0, personTemplates.get(i).getPerson(),
                        personTemplates.get(i), personTemplates.get(i).getInfo()
                        , eventDate, Status.EXPECTED);
                appDb.addEvent(lastEvent);
            }
            while (lastEvent.getDaysLeft() < generateForXdays) {
                lastEvent = personTemplates.get(i).generateEvent(lastEvent.getDate());
                appDb.addEvent(lastEvent);
            }
        }
    }

}
