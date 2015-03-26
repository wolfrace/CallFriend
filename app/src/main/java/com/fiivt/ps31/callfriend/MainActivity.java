package com.fiivt.ps31.callfriend;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.AppDatabase.Person;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity {

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

            db.deleteEvent(allEvents.get(0).getId());
            allEvents = db.getEvents();

            db.deletePerson(p3.getId());
            persons = db.getPersons();
            allEvents = db.getEvents();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            AppDb db = new AppDb(this);
            test(db);
        }
        catch (Exception e) {
            System.out.print(e.getMessage().toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        final ListView listview = (ListView) findViewById(R.id.contactListView);
        Event[] values = new Event[] { new Event("Kill dog", "Kolya"),  new Event("Kit-kat android call", "Petya"),
                new Event("Resource Usage example", "Amjad"),};

        final List<Event> list = new ArrayList<Event>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        final ArrayAdapter adapter = new MySimpleArrayAdapter(this, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(  new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                });
            }

        });

        return true;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_list_contact, parent, false);

            TextView typeView = (TextView) rowView.findViewById(R.id.contactName);
            TextView nameView = (TextView) rowView.findViewById(R.id.contactType);
            ImageView avatar = (ImageView) rowView.findViewById(R.id.contactAvatar);

            Event event = values.get(position);

            nameView.setText(event.getName());
            typeView.setText(event.getText());
            avatar.setImageResource(R.mipmap.ic_user);

            return rowView;
        }

        @Override
        public int getCount() {
            return values.size();
        }

    }

    @Data
    public static class Event {
        private String text;
        private String name;
        private int age;

        public Event(String text, String name) {
            this.text = text;
            this.name = name;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
