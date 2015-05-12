package com.fiivt.ps31.callfriend.AppDatabase2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.fiivt.ps31.callfriend.Utils.Singleton;
import com.fiivt.ps31.callfriend.Utils.Status;

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
            + person.getIdPhoto() + "');");
    }

    public void updatePerson(Person person) {
        db.execSQL("UPDATE person set"
            + " name='"             + person.getName()
            + "' isMale='"          + person.isMale()
            + "' photo='"           + person.getIdPhoto()
            + "' WHERE idPerson='"  + person.getId()
            + "';");
    }

    // Удалит все персонализированные шаблоны и запланированные события
    public void deletePerson(Person person) {
        deleteEventsByPerson(person);
        deletePersonTemplatesByPerson(person);

        db.execSQL("DELETE FROM person"
            + " WHERE idPerson='" + person.getId()
            + "';");
    }

    private void deletePersonTemplatesByPerson(Person person) {
        db.execSQL("DELETE FROM personTemplate"
            + " WHERE idPerson='" + person.getId()
            + "';");
    }

    private void deleteEventsByPerson(Person person) {
        db.execSQL("DELETE FROM event"
            + " WHERE idPerson='" + person.getId()
            + "';");
    }

    public List<Person> getPersons(int limit, int offset) {
        assert limit > 0 : "Limit must be great than 0";
        assert  offset >= 0 : "Offset must be great than 0";
        ArrayList<Person> persons = new ArrayList<Person>();

        Cursor cursor = db.rawQuery("SELECT * FROM person LIMIT " + limit + " OFFSET " + offset, null);

        while(cursor.moveToNext()) {
            Person p = new Person(cursor.getInt(0)
                    , cursor.getString(1)
                    , "someDescription"//todo get from db
                    , cursor.getString(2).equalsIgnoreCase("TRUE")
                    , cursor.getInt(3));
            persons.add(p);
        }

        return persons;
    }

    public Person getPerson(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM person WHERE idPerson='" + id + "';", null);
        cursor.moveToNext();
        return new Person(cursor.getInt(0)
                , cursor.getString(1)
                , "someDescription"// todo get from db
                , cursor.getString(2).equalsIgnoreCase("TRUE")
                , cursor.getInt(3));
    }

    // Templates API
    public void addEventTemplate(EventTemplate eventTemplate) {
        db.execSQL("INSERT INTO eventTemplate(info, canModified, defaultDate, idIcon) VALUES('"
                + eventTemplate.getInfo() + "','"
                + eventTemplate.isCanModified() + "','"
                + eventTemplate.getDefaultDate().getTime() + "','"
                + eventTemplate.getIdIcon() + "');");
    }

    public void updateEventTemplate(EventTemplate eventTemplate) {
        db.execSQL("UPDATE eventTemplate set"
                + " info='" + eventTemplate.getInfo()
                + "' canModified='" + eventTemplate.isCanModified()
                + "' defaultDate='" + eventTemplate.getDefaultDate().getTime()
                + "' idIcon='" + eventTemplate.getIdIcon()
                + "' WHERE idTemplate='" + eventTemplate.getId()
                + "';");
    }

    // Удалятся все персонализированные шаблоны и запланированыые события по ним
    public void deleteEventTemplate(EventTemplate eventTemplate) {
        deletePersonTemplatesByEventTemplate(eventTemplate);

        db.execSQL("DELETE FROM eventTemplate"
                + " WHERE idTemplate='" + eventTemplate.getId()
                + "';");
    }

    private void deleteEventsByPersonTemplate(int id) {
        db.execSQL("DELETE FROM event"
                + " WHERE idPerson='" + id
                + "';");
    }

    // Удалятся все запланированные по персональным шаблонам события
    private void deletePersonTemplatesByEventTemplate(EventTemplate eventTemplate) {
        ArrayList<Integer> personTemplateIds = getPersonTemplateIdsByEventTemplate(eventTemplate);
        for (Integer id : personTemplateIds) {
            deleteEventsByPersonTemplate(id);
        }

        db.execSQL("DELETE FROM personTemplate"
                + " WHERE idTemplate='" + eventTemplate.getId()
                + "';");
    }

    private ArrayList<Integer> getPersonTemplateIdsByEventTemplate(EventTemplate eventTemplate) {
        ArrayList<Integer> result = new ArrayList<Integer>();

        Cursor cursor = db.rawQuery("SELECT idPersonTemplate FROM personTemplate WHERE idTemplate='" + eventTemplate.getId() + "';", null);

        while(cursor.moveToNext()) {
            result.add(cursor.getInt(0));
        }

        return result;
    }

    public List<EventTemplate> getEventTemplates(int limit, int offset) {
        assert limit > 0 : "Limit must be great than 0";
        assert  offset >= 0 : "Offset must be great than 0";
        ArrayList<EventTemplate> eventTemplates = new ArrayList<EventTemplate>();

        Cursor cursor = db.rawQuery("SELECT * FROM eventTemplate LIMIT " + limit + " OFFSET " + offset, null);

        while(cursor.moveToNext()) {
            EventTemplate et = new EventTemplate(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2).equalsIgnoreCase("TRUE"), new Date(cursor.getLong(3)), cursor.getInt(4));
            eventTemplates.add(et);
        }

        return eventTemplates;
    }

    public EventTemplate getEventTemplate(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM eventTemplate WHERE idTemplate='" + id + "'", null);
        cursor.moveToNext();
        return new EventTemplate(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2).equalsIgnoreCase("TRUE"), new Date(cursor.getLong(3)), cursor.getInt(4));
    }

    // PersonTemplate API

    public void addPersonTemplate(PersonTemplate personTemplate) {
        db.execSQL("INSERT INTO personTemplate(idPerson, idTemplate, customDate, cooldown) VALUES('"
            + personTemplate.getPerson().getId()        + "','"
            + personTemplate.getEventTemplate().getId() + "','"
            + personTemplate.getCustomDate().getTime()  + "','"
            + personTemplate.getCooldown().getTime()    + "');");
    }

    public void updatePersonTemplate(PersonTemplate personTemplate) {
        db.execSQL("UPDATE personTemplate set"
            + " idPerson='"                 + personTemplate.getPerson().getId()
            + "' idTemplate='"              + personTemplate.getEventTemplate().getId()
            + "' customDate='"              + personTemplate.getCustomDate().getTime()
            + "' cooldown='"                + personTemplate.getCooldown().getTime()
            + "' WHERE idPersonTemplate='"  + personTemplate.getId()
            + "';");
    }

    // Удалятся все запланированные по персональному шаблону события
    public void deletePersonTemplate(PersonTemplate personTemplate) {
        deleteEventsByPersonTemplate(personTemplate.getId());

        db.execSQL("DELETE FROM personTemplate"
                + " WHERE idPersonTemplate='" + personTemplate.getId()
                + "';");
    }

    public PersonTemplate getPersonTemplate(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM personTemplate WHERE idPersonTemplate='" + id + "'", null);
        if (!cursor.moveToNext())
            return null;
        Person person = getPerson(cursor.getInt(1));
        EventTemplate eventTemplate = getEventTemplate(cursor.getInt(2));

        return new PersonTemplate(cursor.getInt(0), person,
                eventTemplate, new Date(cursor.getLong(3)), new Date(cursor.getLong(4)));
    }

    public ArrayList<PersonTemplate> getPersonTemplates(int limit, int offset) {
        assert limit > 0 : "Limit must be great than 0";
        assert  offset >= 0 : "Offset must be great than 0";
        ArrayList<PersonTemplate> personTemplates = new ArrayList<PersonTemplate>();

        Cursor cursor = db.rawQuery("SELECT * FROM personTemplate LIMIT " + limit + " OFFSET " + offset, null);

        while(cursor.moveToNext()) {
            Person person = getPerson(cursor.getInt(1));
            EventTemplate eventTemplate = getEventTemplate(cursor.getInt(2));

            PersonTemplate pt = new PersonTemplate(cursor.getInt(0), person,
                    eventTemplate, new Date(cursor.getLong(3)), new Date(cursor.getLong(4)));
            personTemplates.add(pt);
        }

        return personTemplates;
    }

    // Event API

    public void addEvent(Event event) {
        db.execSQL("INSERT INTO event(idPerson, idPersonTemplate, info, date, status) VALUES('"
            + event.getPerson().getId()             + "','"
            + event.getPersonTemplate().getId()     + "','"
            + event.getInfo()                       + "','"
            + event.getDate().getTime()             + "','"
            + Status.toInteger(event.getStatus())   + "');");
    }

    public void updateEvent(Event event) {
        db.execSQL("UPDATE event set"
            + " idPerson='"             + event.getPerson().getId()
            + "' idPersonTemplate='"    + event.getPersonTemplate().getId()
            + "' info='"                + event.getInfo()
            + "' date='"                + event.getDate().getTime()
            + "' status='"              + Status.toInteger(event.getStatus())
            + "' WHERE idEvent='"       + event.getId()
            + "';");
    }

    public void deleteEvent(Event event) {
        db.execSQL("DELETE FROM event"
                + " WHERE idEvent='" + event.getId()
                + "';");
    }

    public Event getEvent(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM event WHERE idEvent='" + id + "';", null);
        cursor.moveToNext();
        Person person = getPerson(cursor.getInt(1));
        PersonTemplate personTemplate = getPersonTemplate(cursor.getInt(2));

        return new Event(cursor.getInt(0), person,
                personTemplate, cursor.getString(3), new Date(cursor.getLong(4)), Status.fromInteger(cursor.getInt(5)));
    }

    public ArrayList<Event> getEvents(int limit, int offset) {
        assert limit > 0 : "Limit must be great than 0";
        assert  offset >= 0 : "Offset must be great than 0";
        ArrayList<Event> events = new ArrayList<Event>();

        Cursor cursor = db.rawQuery("SELECT * FROM event LIMIT " + limit + " OFFSET " + offset, null);

        while(cursor.moveToNext()) {
            Person person = getPerson(cursor.getInt(1));
            PersonTemplate personTemplate = getPersonTemplate(cursor.getInt(2));

            Event event = new Event(cursor.getInt(0), person,
                    personTemplate, cursor.getString(3), new Date(cursor.getLong(4)), Status.fromInteger(cursor.getInt(5)));
            events.add(event);
        }

        return events;
    }
}