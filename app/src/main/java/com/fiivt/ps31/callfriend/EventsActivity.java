package com.fiivt.ps31.callfriend;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.AppDatabase.Person;

import java.util.Date;


public class EventsActivity extends ActionBarActivity {

    public AppDb database;

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

            Event e2 = new Event("Позвать в кино", new Date(), p2);
            db.addEvent(e2);

            Event e3 = new Event("Поздавить с днем вафли", new Date(), p3);
            db.addEvent(e3);

            Event e4 = new Event("Позвать в мак", new Date(), p3);
            db.addEvent(e4);

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
        database = new AppDb(this);
        test(database);
        setContentView(R.layout.activity_main);

        ListView eventsListView = (ListView) findViewById(R.id.events_list_view);
        final List<Event> event = database.getEvents();
        ArrayAdapter adapter = new MySimpleArrayAdapter(this, event);
        eventsListView.setAdapter(adapter);
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


    @Data
    @NoArgsConstructor
    static class EventViewHolder {
        private TextView name;
        private TextView title;
        private ImageView image;
        private TextView daysLeft;

        public void setEventValues(Event event) {
            name.setText(event.getPerson().getName());
            title.setText(event.getTitle());
            //image.setImageResource(R.mipmap.ic_user);
        }
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<Event> {
        private final Context context;
        private final List<Event> values;

        public MySimpleArrayAdapter(Context context, List<Event> values) {
            super(context, R.layout.row_list_contact, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getViewWithHolder(convertView, parent);
            EventViewHolder viewHolder = (EventViewHolder) view.getTag();
            Event event = values.get(position);
            viewHolder.setEventValues(values.get(position));
            return view;
        }

        private View getViewWithHolder(View convertView, ViewGroup parent) {
            if (convertView == null) {
                View view = createNewView(parent);
                initializeHolder(view);
                return view;
            } else {
                return convertView;
            }
        }

        private View createNewView(ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.row_list_contact, parent, false);
        }

        private EventViewHolder initializeHolder(View view) {
            EventViewHolder holder = new EventViewHolder();
            holder.setName((TextView) view.findViewById(R.id.event_list_contact_name));
            holder.setTitle((TextView) view.findViewById(R.id.event_list_event_title));
            holder.setDaysLeft((TextView) view.findViewById(R.id.events_days_left));
            //holder.setImage((ImageView) view.findViewById(R.id.contactAvatar));
            view.setTag(holder);
            return holder;
        }

        @Override
        public int getCount() {
            return values.size();
        }

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
