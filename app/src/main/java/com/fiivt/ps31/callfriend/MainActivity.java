package com.fiivt.ps31.callfriend;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
        return true;
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
