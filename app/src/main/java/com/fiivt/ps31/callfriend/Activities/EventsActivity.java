package com.fiivt.ps31.callfriend.Activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.BaseActivity;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class EventsActivity extends BaseActivity {

    public AppDb database;

    private View eventListEmptyNotify;
    private EventsListView eventsListUrgently;
    private EventsListView eventsListSoon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);
        eventListEmptyNotify = findViewById(R.id.events_not_existing_notify);
        initEventsLists();

        database = new AppDb(this);

        String partition = getIntent().getStringExtra("partition");
/*        if (partition.equals("birthday"))
            addEventsToView(database.getEvents(Integer.MAX_VALUE, 0));
        else if (partition.equals("special"))
            addEventsToView(database.getEvents(Integer.MAX_VALUE, 0));
        else
            addEventsToView(database.getEvents(Integer.MAX_VALUE, 0));*/
        addEventsToView(database.getEvents(Integer.MAX_VALUE, 0, partition));
    }

    public void dismissEvent(Event event) {
        //todo remove pushes
        event.setStatus(Status.DELETED);
        removeEventFromView(event);
    }

    public void acceptEvent(Event event) {
        //todo remove pushes
        event.setStatus(Status.ACHIEVED);
        database.updateEvent(event);
        removeEventFromView(event);
    }

    public void putOffEvent(Event event) {
        event.putOff(TimeUnit.HOURS, 25);
        //todo update pushes
        database.updateEvent(event);
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
            if (event.getDaysLeft() <= 3) {
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
        EventsListView.OnEventClickListener eventClickListener = new EventsListView.OnEventClickListener() {
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
       // getMenuInflater().inflate(R.menu.menu_, menu);

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
       /* if (id == R.id.action_show_contacts) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
