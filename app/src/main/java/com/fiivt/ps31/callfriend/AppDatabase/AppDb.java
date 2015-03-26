package com.fiivt.ps31.callfriend.AppDatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fiivt.ps31.callfriend.Utils.Singleton;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.*;

/**
 * Created by Egor on 24.03.2015.
 */

public class AppDb extends Singleton {
    private SQLiteDatabase db;
    private String dbPath = "AppDb.db";

    public AppDb(Context c) {
        c.deleteDatabase(dbPath); // временно
        db = c.openOrCreateDatabase(dbPath, c.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS person(idP INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name VARCHAR, dob DATE, isMale BOOLEAN);");
        db.execSQL("CREATE TABLE IF NOT EXISTS event(idE INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, title VARCHAR, date DATE, idP INTEGER NOT NULL);");
    }

    public Person getPerson(Integer id) {
        Cursor cursor = db.rawQuery("SELECT * FROM person WHERE idP='" + id + "'", null);
        cursor.moveToNext();
        Person p = new Person(cursor.getInt(0), cursor.getString(1), new Date(cursor.getLong(2)), cursor.getString(3).equalsIgnoreCase("TRUE"));
        return p;
    }

    public List<Event> getExpiredEvents() {
        ArrayList<Event> events = new ArrayList<Event>();

        Cursor cursor = db.rawQuery("SELECT * FROM event WHERE date <=" + (new Date()), null);

        while(cursor.moveToNext()) {

//            Event e = new Event(cursor.getString(0), new Date(cursor.getLong(1)), cursor.getInt(2) > 0);
//            events.add(p);
        }

        return events;
    }

    public void addPerson(Person person) {
        db.execSQL("INSERT INTO person(name, dob, isMale) VALUES('"
                + person.getName() +  "','"
                + person.getDob() + "','"
                + person.isMale() + "');");
    }

    public void addEvent(Event event) {
        db.execSQL("INSERT INTO event(title, date, idP) VALUES('"
                + event.getTitle() +  "','"
                + event.getDate() + "','"
                + event.getPerson().getId() + "');");
    }

    public List<Person> getPersons() {
        ArrayList<Person> persons = new ArrayList<Person>();

        Cursor cursor = db.rawQuery("SELECT * FROM person", null);

        while(cursor.moveToNext()) {
            Person p = new Person(cursor.getInt(0), cursor.getString(1), new Date(cursor.getLong(2)), cursor.getString(3).equalsIgnoreCase("TRUE"));
            persons.add(p);
        }

        return persons;
    }

}
