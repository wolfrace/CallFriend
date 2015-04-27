package com.fiivt.ps31.callfriend;

import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.EventsListView.OnEventClickListener;

import java.util.Date;


public class EventsActivity extends ActionBarActivity {

    public AppDb database;

    private EventsListView eventsListUrgently;
    private EventsListView eventsListSoon;

    public void test(AppDb db) {
        Person tmpPerson = new Person("Kolya Lobkov", new Date(), true);
        Person tmpPerson2 = new Person("Lena Lobkova", new Date(), false);
        Person tmpPerson3 = new Person("Danil Lobkov", new Date(), true);

        db.addPerson(tmpPerson);
        db.addPerson(tmpPerson2);
        db.addPerson(tmpPerson3);

        List<Person> persons = db.getPersons();
        {
            Person p = db.getPerson(1);
            Person p2 = db.getPerson(2);
            Person p3 = db.getPerson(3);
            Event e = new Event("Поздавить с днем вафли", new Date(), p);
            db.addEvent(e);

            List<Event> oldEvents = db.getExpiredEvents();
            List<Event> allEvents = db.getEvents();

            Event e2 = new Event("Позвать в кино", new Date(1430050904956L), p2);
            db.addEvent(e2);


            Event e4 = new Event("Позвать в мак", new Date(1450050904956L), p3);
            db.addEvent(e4);


            Event e3 = new Event("Поздавить с днем вафли", new Date(1430450904956L), p3);
            db.addEvent(e3);

//            db.deleteEvent(allEvents.get(0).getId());
            allEvents = db.getEvents();

//            db.deletePerson(p3.getId());
            persons = db.getPersons();
            allEvents = db.getEvents();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);
        initEventsLists();

        database = new AppDb(this);
        // todo Remove test
        test(database);
        // test end
        addEvents(database.getEvents());
    }

    public void dismissEvent(Event event) {
        //todo remove(update) event from db and view
    }

    public void acceptEvent(Event event) {
        //todo remove(update) event from db and view
    }

    public void putOffEvent(Event event) {
        //todo 1) show time picker?; 2) update event in db; 3) update view;
    }

    public void addEvent(Event event) {
        addEvents(Collections.singleton(event));
    }

    public void addEvents(Collection<Event> events) {
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
