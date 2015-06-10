package com.fiivt.ps31.callfriend.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.fiivt.ps31.callfriend.Activities.EventsActivity;
import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate;
import com.fiivt.ps31.callfriend.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Egor on 27.05.2015.
 */

/**
 * example code:
 *
 * new Thread(new Runnable() {
 *      Context context;
 *      @Override
 *      public void run() {
 *          EventsGenerator.generate(context);
 *      }
 *      public Runnable init(Context context){
 *          this.context = context;
 *          return this;
 *      }
 * }.init(context)).start();
 */

public class EventsGenerator {
    static final String LOG_TAG = "EventGeneratorLog";
    static final int generateForXdays = 30;

    static public void generate(Context context){
        Log.e(LOG_TAG, "generateEventsFromEventsGenerator");

        AppDb appDb = new AppDb(context);
        List<PersonTemplate> personTemplates = appDb.getEnabledPersonTemplates(Integer.MAX_VALUE, 0);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        GregorianCalendar today = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));
        for (int i = 0; i < personTemplates.size(); ++i) {
            Event lastEvent = appDb.getLastEventByPersonTemplate(personTemplates.get(i).getId());
            if (lastEvent == null) {//todo ref
                Date eventDate = personTemplates.get(i).getCustomDate();
                while (eventDate.getTime() < today.getTime().getTime()) {
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

            Event todayEvent = appDb.getTodayEventByPersonTemplate(personTemplates.get(i).getId());
            if(todayEvent!= null) {
                Log.e(LOG_TAG, "generateNotifications");
                generateNotification(todayEvent, context);
            }
        }
    }

    static private void generateNotification(Event lastEvent, Context context) {
        Intent intent = new Intent(context, EventsActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification n  = new Notification.Builder(context)
                .setContentTitle("CallFriend!")
                .setContentText(lastEvent.getPerson().getName().concat(context.getString(R.string.notification_suffix)).concat(lastEvent.getInfo()))
                .setSmallIcon(R.drawable.callfriend_app_icon)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
//                .addAction(R.drawable.callfriend_app_icon, "Call", pIntent)
//                .addAction(R.drawable.callfriend_app_icon, "More", pIntent)
//                .addAction(R.drawable.callfriend_app_icon, "And more", pIntent).build();


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }
}
