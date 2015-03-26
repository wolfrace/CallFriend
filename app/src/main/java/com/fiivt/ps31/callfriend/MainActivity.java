package com.fiivt.ps31.callfriend;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fiivt.ps31.callfriend.AppDatabase.DatabaseHelper;
import com.fiivt.ps31.callfriend.AppDatabase.Person;

import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            DatabaseHelper db = new DatabaseHelper(this);
            Person tmpPerson = new Person("Kolya Lobkov", new Date(), true);
            Person tmpPerson2 = new Person("Lena Lobkova", new Date(), false);

            db.addPerson(tmpPerson);
            db.addPerson(tmpPerson2);

            List<Person> persons = db.getPersons();
            int a = 2;
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
