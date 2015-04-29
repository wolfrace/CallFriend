package com.fiivt.ps31.callfriend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import android.view.Gravity;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;

import java.io.Console;
import java.util.Date;
import java.util.List;

/**
 * Created by Данил on 24.04.2015.
 */
public class PersonActivity extends Activity {


    public AppDb database;

    public void test(AppDb db) {
        Person tmpPerson = new Person("Kolya Lobkov", new Date(), true);
        Person tmpPerson2 = new Person("Lena Lobkova", new Date(), false);
        Person tmpPerson3 = new Person("Danil Lobkov", new Date(), true);
        Person tmpPerson4 = new Person("Kolya Lobkov", new Date(), true);
        Person tmpPerson5 = new Person("Lena Lobkova", new Date(), false);
        Person tmpPerson6 = new Person("Danil Lobkov", new Date(), true);
        Person tmpPerson7 = new Person("Kolya Lobkov", new Date(), true);
        Person tmpPerson8 = new Person("Lena Lobkova", new Date(), false);
        Person tmpPerson9 = new Person("Danil Lobkov", new Date(), true);
        Person tmpPerson10 = new Person("Kolya Lobkov", new Date(), true);
        Person tmpPerson11 = new Person("Lena Lobkova", new Date(), false);
        Person tmpPerson12 = new Person("Danil Lobkov", new Date(), true);
        Person tmpPerson13 = new Person("Kolya Lobkov", new Date(), true);
        Person tmpPerson14 = new Person("Lena Lobkova", new Date(), false);
        Person tmpPerson15 = new Person("Danil Lobkov", new Date(), true);

        db.addPerson(tmpPerson);
        db.addPerson(tmpPerson2);
        db.addPerson(tmpPerson3);
        db.addPerson(tmpPerson4);
        db.addPerson(tmpPerson5);
        db.addPerson(tmpPerson6);
        db.addPerson(tmpPerson7);
        db.addPerson(tmpPerson8);
        db.addPerson(tmpPerson9);
        db.addPerson(tmpPerson10);
        db.addPerson(tmpPerson11);
        db.addPerson(tmpPerson12);
        db.addPerson(tmpPerson13);
        db.addPerson(tmpPerson14);
        db.addPerson(tmpPerson15);

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
        setContentView(R.layout.person_list_layout);

        ImageView addPersonButton = (ImageView)findViewById(R.id.person_add_image);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(PersonActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });
        ListView personsListView = (ListView) findViewById(R.id.person_list_view);
        final List<Person> person = database.getPersons();
        ArrayAdapter personAdapter = new PersonArrayAdapter(this, person);
        personsListView.setAdapter(personAdapter);
    }

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
    static class PersonViewHolder {
        private TextView name;
        private ImageView image;

        public void setPersonValues(Person person) {
            name.setText(person.getName());
            //image.setImageResource(R.mipmap.ic_user);
        }
    }

    public class PersonArrayAdapter extends ArrayAdapter<Person> {
        private final Context context;
        private final List<Person> values;

        public PersonArrayAdapter(Context context, List<Person> values) {
            super(context, R.layout.row_list_cont, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getViewWithHolder(convertView, parent);
            PersonViewHolder viewHolder = (PersonViewHolder) view.getTag();
            Person person = values.get(position);
            viewHolder.setPersonValues(person);
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
            return inflater.inflate(R.layout.row_list_cont, parent, false);
        }

        private PersonViewHolder initializeHolder(View view) {
            PersonViewHolder holder = new PersonViewHolder();
            holder.setName((TextView) view.findViewById(R.id.person_list_contact_name));
            //holder.setImage((ImageView) view.findViewById(R.id.contactAvatar));
            view.setTag(holder);
            return holder;
        }

        @Override
        public int getCount() {
            return values.size();
        }
    }

    public void buttonAddOnClick(View v)
    {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context,
                "This is Toast Notification", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
