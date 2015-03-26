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


//public DBAdapter(Context context) {
//        this.adapterContext = context;
//        DB_PATH = adapterContext.getFilesDir().getAbsolutePath().replace("files", "databases")
//        + File.separator;
//        }

public class DatabaseHelper extends Singleton {
    private SQLiteDatabase db;
    //private String dbPath = "data/data/com.fiivt.ps31.callfriend/databases/AppDb.db";
    private String dbPath = "AppDb.db";

    public DatabaseHelper(Context c) {
//        File file = new File(dbPath);
//        if (file.exists() && !file.isDirectory())
        db = c.openOrCreateDatabase(dbPath, c.MODE_PRIVATE, null);
        //db = openOrCreateDatabase(dbPath, c.);
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
            int a = cursor.getColumnCount();
            Person p = new Person(cursor.getString(0), new Date(cursor.getLong(1)), cursor.getInt(2) > 0);
            persons.add(p);
        }

        return persons;
    }

}
