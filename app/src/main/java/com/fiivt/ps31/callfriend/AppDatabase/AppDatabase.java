package com.fiivt.ps31.callfriend.AppDatabase;

import android.content.Context;
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
    private SQLiteDatabase _db;

    public AppDatabase()
    {
        _db = openOrCreateDatabase("AppDb", null, null);
        _db.execSQL("CREATE TABLE IF NOT EXISTS person(name VARCHAR, dob DATE, isMale BOOLEAN);");
        _db.execSQL("CREATE TABLE IF NOT EXISTS event(title VARCHAR, date DATE, person VARCHAR);");
    }

    public Person GetPerson(String name, Date dob)
    {
        Person p = new Person("vasya", new Date(), true);
        return p;
    }

    public List<Person> GetPersons()
    {
        ArrayList<Person> lPerson = new ArrayList<Person>();
        return lPerson;
    }

    public boolean addPerson(Person person)
    {
        _db.execSQL("INSERT INTO person VALUES('"
            + person.getName(), "','"
            + person.getDob(), "','"
            + person.isMale(), "');");
    }
}
