package com.fiivt.ps31.callfriend;

import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fiivt.ps31.callfriend.AppDatabase2.*;
import com.fiivt.ps31.callfriend.EventsListView.OnEventClickListener;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class EventsActivity extends ActionBarActivity {

    public AppDb database;

    private View eventListEmptyNotify;
    private EventsListView eventsListUrgently;
    private EventsListView eventsListSoon;

    public void test(AppDb db) {
        Person tmpPerson = new Person("Kolya Lobkov", "tovarish", true, 1);
        Person tmpPerson2 = new Person("Lena Lobkova", "mati", false, 2);
        Person tmpPerson3 = new Person("Danil Lobkov", "brat",true, 3);

        db.addPerson(tmpPerson);
        db.addPerson(tmpPerson2);
        db.addPerson(tmpPerson3);

        List<Person> persons = db.getPersons(100, 0);
        {
            EventTemplate et = new EventTemplate("Поздравить с днем вафли", true, new Date(), 0);
            EventTemplate et2 = new EventTemplate("Позвать синячить", true, new Date(), 0);
            db.addEventTemplate(et);
            db.addEventTemplate(et2);

            et = db.getEventTemplate(1);
            et2 = db.getEventTemplate(2);


            Person p = db.getPerson(1);
            Person p2 = db.getPerson(2);
            Person p3 = db.getPerson(3);

            PersonTemplate pt = new PersonTemplate(p, et, new Date(), new Date(TimeUnit.DAYS.toMillis(1)));
            PersonTemplate pt2 = new PersonTemplate(p2, et, new Date(), new Date(TimeUnit.DAYS.toMillis(2)));
            PersonTemplate pt3 = new PersonTemplate(p3, et, new Date(), new Date(TimeUnit.DAYS.toMillis(3)));
            PersonTemplate pt4 = new PersonTemplate(p3, et2, new Date(), new Date(TimeUnit.DAYS.toMillis(3)));

            db.addPersonTemplate(pt);
            db.addPersonTemplate(pt2);
            db.addPersonTemplate(pt3);
            db.addPersonTemplate(pt4);

            pt = db.getPersonTemplate(1);
            pt2 = db.getPersonTemplate(2);
            pt3 = db.getPersonTemplate(3);
            pt4 = db.getPersonTemplate(4);

            Event e = pt.generateEvent();
            db.addEvent(e);

            List<Event> allEvents = db.getEvents(Integer.MAX_VALUE, 0);

            Event e2 = pt2.generateEvent();
            db.addEvent(e2);


            Event e4 = pt4.generateEvent();
            db.addEvent(e4);


            Event e3 = pt3.generateEvent();
            db.addEvent(e3);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);
        eventListEmptyNotify = findViewById(R.id.events_not_existing_notify);
        initEventsLists();

        database = new AppDb(this);
        // todo Remove test
        test(database);
        // test end
        addEventsToView(database.getEvents(Integer.MAX_VALUE, 0));
    }

    public void dismissEvent(Event event) {
        //todo remove(update template) from db
        removeEventFromView(event);
    }

    public void acceptEvent(Event event) {
        //todo remove(update template) from db
        removeEventFromView(event);
    }

    public void putOffEvent(Event event) {
        event.putOff(TimeUnit.HOURS, 25);
        //todo save into db
        removeEventFromView(event);
        addEventToView(event);
    }

    public void removeEventFromView(Event event) {
        eventsListUrgently.removeById(event.getId());
        eventsListSoon.removeById(event.getId());
        notifyOnEventListChanged();
    }

    public void addEventToView(Event event) {
        addEventsToView(Collections.singleton(event));
    }

    public void addEventsToView(Collection<Event> events) {
        List<Event> soon = new ArrayList<Event>();
        List<Event> urgently = new ArrayList<Event>();

        for(Event event: events) {
            if (event.getDaysLeft() <= 0) {
                urgently.add(event);
            } else {
                soon.add(event);
            }
        }

        if (!soon.isEmpty()) {
            eventsListSoon.addAll(soon);
        }

        if (!urgently.isEmpty()) {
            eventsListUrgently.addAll(urgently);
        }
        notifyOnEventListChanged();
    }

    private void notifyOnEventListChanged() {
        boolean isEmpty = (eventsListSoon.isEmpty() && eventsListUrgently.isEmpty());
        eventListEmptyNotify.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void initEventsLists() {
        OnEventClickListener eventClickListener = new OnEventClickListener() {
            @Override
            public void onClick(Event event) {
                showEventActionsDialog(event);
            }
        };

        eventsListUrgently = (EventsListView) findViewById(R.id.events_list_urgently);
        eventsListSoon = (EventsListView) findViewById(R.id.events_list_soon);

        eventsListSoon.setClickListener(eventClickListener);
        eventsListUrgently.setClickListener(eventClickListener);
    }

    private void showEventActionsDialog(final Event event) {
        FragmentManager manager = getFragmentManager();
        EventActionDialog dialog = new EventActionDialog();
        dialog.setClickListener(new EventActionDialog.EventActionClickListener() {
            @Override
            public void onClick(EventActionDialog.EventActionType actionType) {
                processEventAction(event, actionType);
            }
        });
        dialog.show(manager, "event-actions");
    }

    private void processEventAction(Event event, EventActionDialog.EventActionType actionType) {
        switch (actionType) {
            case PUT_OFF:
                putOffEvent(event);
                break;
            case ACCEPT:
                acceptEvent(event);
                break;
            case DISMISS:
                dismissEvent(event);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        eventsListView.setOnItemClickListener(  new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//            }
//        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_contacts) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
