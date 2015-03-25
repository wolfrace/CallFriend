package com.fiivt.ps31.callfriend.AppDatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fiivt.ps31.callfriend.Utils.Singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.*;

/**
 * Created by Egor on 24.03.2015.
 */
public class AppDatabase extends Singleton{
    private SQLiteDatabase db;

    public AppDatabase() {
        db = openOrCreateDatabase("AppDb", null, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS person(name VARCHAR, dob DATE, isMale BOOLEAN);");
        db.execSQL("CREATE TABLE IF NOT EXISTS event(title VARCHAR, date DATE, person VARCHAR);");
    }

    public Person GetPerson(String name, Date dob) {
        Person p = new Person("vasya", new Date(), true);
        return p;
    }

    public void addPerson(Person person) {
        db.execSQL("INSERT INTO person VALUES('"
                + person.getName()
                +  "','"  + person.getDob() + "','"
                + person.isMale() + "');");
    }

    public void addEvent(Event event) {
        db.execSQL("INSERT INTO event VALUES('"
                + event.getTitle() +  "','"
                + event.getDate()
                + "','"  + event.getPerson().getName() + "');");
    }

    public List<Person> getPersons() {
        ArrayList<Person> persons = new ArrayList<Person>();

        Cursor cursor = db.rawQuery("SELECT * FROM person", null);

        while(cursor.moveToNext()) {
            Person p = new Person(cursor.getString(1), new Date(cursor.getLong(2)), cursor.getInt(3) > 0);
            persons.add(p);
        }

        return persons;
    }

}
