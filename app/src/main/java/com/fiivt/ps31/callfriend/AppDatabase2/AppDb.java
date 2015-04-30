package com.fiivt.ps31.callfriend.AppDatabase2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.fiivt.ps31.callfriend.Utils.Singleton;
import com.fiivt.ps31.callfriend.Utils.Status;
import lombok.ToString;

import java.sql.PreparedStatement;
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
        ContentValues insertValues = new ContentValues();
        insertValues.put("name", person.getName());
        insertValues.put("isMale", person.isMale());
        insertValues.put("photo", person.getIdPhoto());

        long id = db.insert("person", null, insertValues);
        person.setId((int)id);
    }

    public void updatePerson(Person person) {
        ContentValues newValues = new ContentValues();
        newValues.put("name", person.getName());
        newValues.put("isMale", person.getIdPhoto());
        newValues.put("photo", person.getName());

        db.update("person", newValues, "idPerson=".concat(Integer.toString(person.getId())), null);
    }

    // Удалит все персонализированные шаблоны и запланированные события
    public void deletePerson(Person person) {
        deleteEventsByPerson(person);
        deletePersonTemplatesByPerson(person);

        db.delete("person", "idPerson=".concat(Integer.toString(person.getId())), null);
    }

    private void deletePersonTemplatesByPerson(Person person) {
        db.delete("personTemplate", "idPerson=".concat(Integer.toString(person.getId())), null);
    }

    private void deleteEventsByPerson(Person person) {
        db.delete("event", "idPerson=".concat(Integer.toString(person.getId())), null);
    }

    public List<Person> getPersons(int limit, int offset) {
        assert limit > 0 : "Limit must be great than 0";
        assert  offset >= 0 : "Offset must be great than 0";
        ArrayList<Person> persons = new ArrayList<Person>();

        Cursor cursor = db.rawQuery("SELECT * FROM person LIMIT " + limit + " OFFSET " + offset, null);

        while(cursor.moveToNext()) {
            Person p = new Person(cursor.getInt(0), cursor.getString(1), cursor.getString(2).equalsIgnoreCase("TRUE"), cursor.getInt(3));
            persons.add(p);
        }

        return persons;
    }

    public Person getPerson(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM person WHERE idPerson='" + id + "';", null);
        cursor.moveToNext();
        return new Person(cursor.getInt(0)
                , cursor.getString(1)
                , cursor.getString(2).equalsIgnoreCase("TRUE")
                , cursor.getInt(3));
    }

    // Templates API
    public void addEventTemplate(EventTemplate eventTemplate) {
        ContentValues insertValues = new ContentValues();
        insertValues.put("info", eventTemplate.getInfo());
        insertValues.put("canModified", eventTemplate.isCanModified());
        insertValues.put("defaultDate", eventTemplate.getDefaultDate().getTime());
        insertValues.put("idIcon", eventTemplate.getIdIcon());

        long id = db.insert("eventTemplate", null, insertValues);
        eventTemplate.setId((int) id);
    }

    public void updateEventTemplate(EventTemplate eventTemplate) {
        ContentValues newValues = new ContentValues();
        newValues.put("info", eventTemplate.getInfo());
        newValues.put("canModified", eventTemplate.isCanModified());
        newValues.put("defaultDate", eventTemplate.getDefaultDate().getTime());
        newValues.put("idIcon", eventTemplate.getIdIcon());

        db.update("eventTemplate", newValues, "idTemplate=".concat(Integer.toString(eventTemplate.getId())), null);
    }

    // Удалятся все персонализированные шаблоны и запланированыые события по ним
    public void deleteEventTemplate(EventTemplate eventTemplate) {
        deletePersonTemplatesByEventTemplate(eventTemplate);

        db.delete("eventTemplate", "idTemplate=".concat(Integer.toString(eventTemplate.getId())), null);
    }

    private void deleteEventsByPersonTemplate(int id) {
        db.delete("event", "idPerson=".concat(Integer.toString(id)), null);
    }

    // Удалятся все запланированные по персональным шаблонам события
    private void deletePersonTemplatesByEventTemplate(EventTemplate eventTemplate) {
        ArrayList<Integer> personTemplateIds = getPersonTemplateIdsByEventTemplate(eventTemplate);
        for (Integer id : personTemplateIds) {
            deleteEventsByPersonTemplate(id);
        }

        db.delete("personTemplate", "idTemplate=".concat(Integer.toString(eventTemplate.getId())), null);
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
        ContentValues insertValues = new ContentValues();
        insertValues.put("idPerson", personTemplate.getPerson().getId());
        insertValues.put("idTemplate", personTemplate.getEventTemplate().getId());
        insertValues.put("customDate", personTemplate.getCustomDate().getTime());
        insertValues.put("cooldown", personTemplate.getCooldown().getTime());

        long id = db.insert("personTemplate", null, insertValues);
        personTemplate.setId((int) id);
    }

    public void updatePersonTemplate(PersonTemplate personTemplate) {
        ContentValues newValues = new ContentValues();
        newValues.put("idPerson", personTemplate.getPerson().getId());
        newValues.put("idTemplate", personTemplate.getEventTemplate().getId());
        newValues.put("customDate", personTemplate.getCustomDate().getTime());
        newValues.put("cooldown", personTemplate.getCooldown().getTime());

        db.update("personTemplate", newValues, "idPersonTemplate=".concat(Integer.toString(personTemplate.getId())), null);
    }

    // Удалятся все запланированные по персональному шаблону события
    public void deletePersonTemplate(PersonTemplate personTemplate) {
        deleteEventsByPersonTemplate(personTemplate.getId());

        db.delete("personTemplate", "idPersonTemplate=".concat(Integer.toString(personTemplate.getId())), null);
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
        ContentValues insertValues = new ContentValues();
        insertValues.put("idPerson", event.getPerson().getId());
        insertValues.put("idPersonTemplate", event.getPersonTemplate().getId());
        insertValues.put("info", event.getInfo());
        insertValues.put("date", event.getDate().getTime());
        insertValues.put("status", Status.toInteger(event.getStatus()));

        long id = db.insert("event", null, insertValues);
        event.setId((int) id);
    }

    public void updateEvent(Event event) {
        ContentValues newValues = new ContentValues();
        newValues.put("idPerson", event.getPerson().getId());
        newValues.put("idPersonTemplate", event.getPersonTemplate().getId());
        newValues.put("info", event.getInfo());
        newValues.put("date", event.getDate().getTime());
        newValues.put("status", Status.toInteger(event.getStatus()));

        db.update("event", newValues, "idEvent=".concat(Integer.toString(event.getId())), null);
    }

    public void deleteEvent(Event event) {
        db.delete("event", "idEvent=".concat(Integer.toString(event.getId())), null);
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