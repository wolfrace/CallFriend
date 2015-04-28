package com.fiivt.ps31.callfriend.AppDatabase2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.fiivt.ps31.callfriend.AppDatabase.*;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.Utils.Singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Egor on 23.04.2015.
 */
public class AppDb extends  Singleton {
    private SQLiteDatabase db;
    private String dbPath = "AppDb_new.db";

    public AppDb(Context c) {
        c.deleteDatabase(dbPath); // dropbase
        db = c.openOrCreateDatabase(dbPath, c.MODE_PRIVATE, null);
        // Персона имеет имя, пол, фотографию
        db.execSQL("CREATE TABLE IF NOT EXISTS person(idPerson INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name VARCHAR, isMale BOOLEAN, photo BLOB);");

        // Шаблон события имеет информацию о событии, дату по умолчанию, идентификатор иконки и флаг, оповещающий о возможности менять дату и кулдаун события (для Нового Года, например, нельзя)
        db.execSQL("CREATE TABLE IF NOT EXISTS eventTemplate(idTemplate INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, info VARCHAR, canModified BOOLEAN, defaultDate DATE, idIcon INTEGER);");

        // Персональный шаблон хранит настроенный для конкретного человека шаблон события. Содержит идентификатор персоны, шаблона события, дату начала и кулдаун
        db.execSQL("CREATE TABLE IF NOT EXISTS personTemplate(idPersonTemplate INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, idPerson INTEGER, idTemplate INTEGER, customDate DATE, cooldown DATE);");

        // Событие хранит идентификаторы персоны, персонального шаблона, дату события и статус события
        db.execSQL("CREATE TABLE IF NOT EXISTS event(idEvent INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, idPerson INTEGER, idPersonTemplate INTEGER, info VARCHAR, date DATE, status INTEGER);");
    }


    //  Persons API
    public void addPerson(Person person) {
        db.execSQL("INSERT INTO person(name, isMale, photo) VALUES('"
            + person.getName()  + "','"
            + person.isMale()   + "','"
            + person.getPhoto() + "');");
    }

    public void updatePerson(Person person) {
        db.execSQL("UPDATE person set"
            + " name='"             + person.getName()
            + "' isMale='"          + person.isMale()
            + "' photo='"           + person.getPhoto()
            + "' WHERE idPerson='"  + person.getId()
            + "';");
    }

    // Удалит все персонализированные шаблоны и запланированные события
    public void deletePerson(Person person) {
        deleteEventsByPerson(person);
        deletePersonEventsByPerson(person);

        db.execSQL("DELETE FROM person"
            + " WHERE idPerson='" + person.getId()
            + "';");
    }

    private void deletePersonEventsByPerson(Person person) {
        db.execSQL("DELETE FROM personTemplate"
            + " WHERE idPerson='" + person.getId()
            + "';");
    }

    private void deleteEventsByPerson(Person person) {
        db.execSQL("DELETE FROM event"
            + " WHERE idPerson='" + person.getId()
            + "';");
    }

    public List<Person> getPersons(int limit) {
        assert limit > 0;
        ArrayList<Person> person = new ArrayList<Person>();

        Cursor cursor = db.rawQuery("SELECT * FROM person LIMIT " + limit, null);

        while(cursor.moveToNext()) {
            Person p = new Person(cursor.getInt(0), cursor.getString(1), cursor.getString(2).equalsIgnoreCase("TRUE"), cursor.getBlob(3));
            person.add(p);
        }

        return person;
    }

    public Person getPerson(Integer id) {
        Cursor cursor = db.rawQuery("SELECT * FROM person WHERE idPerson='" + id + "';", null);
        cursor.moveToNext();
        Person p = new Person(cursor.getInt(0)
                , cursor.getString(1)
                , cursor.getString(2).equalsIgnoreCase("TRUE")
                , cursor.getBlob(3));
    }

    // Templates API
    public void AddEventTemplate(EventTemplate eventTemplate) {
        db.execSQL("INSERT INTO eventTemplate(info, canModified, defaultDate, idIcon) VALUES('"
            + eventTemplate.getInfo()                   + "','"
            + eventTemplate.isCanModified()             + "','"
            + eventTemplate.getDefaultDate().getTime()  + "','"
            + eventTemplate.getIdIcon()                 + "');");
    }

    public void updateEventTemplate(EventTemplate eventTemplate) {

    }

    public void deleteEventTemplate(EventTemplate eventTemplate) {

    }

    public List<EventTemplate> getEventTemplates() {
        return null;
    }

    public EventTemplate getEventTemplate() {
        return null;
    }
}