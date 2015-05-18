package com.fiivt.ps31.callfriend.AppDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fiivt.ps31.callfriend.Activities.FriendEdit;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.Singleton;
import com.fiivt.ps31.callfriend.Utils.Status;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Egor on 23.04.2015.
 */
public class AppDb extends Singleton {
    private SQLiteDatabase db;
    private String dbPath = "AppDb_new.db";

    public AppDb(Context c) {
        //c.deleteDatabase(dbPath); // dropbase
        initDb(c);
    }


    public void clearDb(Context c) {
        c.deleteDatabase(dbPath);
        initDb(c);
    }

    private void initDb(Context c) {
        boolean isExists = isDatabaseExists(c);
        db = c.openOrCreateDatabase(dbPath, Context.MODE_PRIVATE, null);// dropbase
        db.execSQL("CREATE TABLE IF NOT EXISTS person(idPerson INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name VARCHAR, description STRING, isMale BOOLEAN, photo BLOB);");

        db.execSQL("CREATE TABLE IF NOT EXISTS eventTemplate(idTemplate INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, info VARCHAR, canModified BOOLEAN, defaultDate DATE, idIcon INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS personTemplate(idPersonTemplate INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, idPerson INTEGER, idTemplate INTEGER, customDate DATE, cooldown DATE, remindTime INTEGER, enabled BOOLEAN, info VARCHAR);");

        db.execSQL("CREATE TABLE IF NOT EXISTS event(idEvent INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, idPerson INTEGER, idPersonTemplate INTEGER, info VARCHAR, date DATE, status INTEGER);");
        if (!isExists) {
            generateDefaultTemplates();
        }
    }

    private boolean isDatabaseExists(Context context) {
        File dbFile = context.getDatabasePath(dbPath);
        return dbFile.exists();
    }

    private void generateDefaultTemplates() {
        List<EventTemplate> templates = generateListOfDefaultTemplates();
        for (EventTemplate template: templates) {
            addEventTemplate(template);
        }
    }

    private List<EventTemplate> generateListOfDefaultTemplates() {
        List<EventTemplate> templates = new ArrayList<EventTemplate>();
        templates.add(createEventTemplate("День рождения", R.drawable.ic_event_birthday));
        templates.add(createEventTemplate("Новый год", 11, 31, R.drawable.ic_event_xmass));
        templates.add(createEventTemplate("День ангела", R.drawable.ic_event_angel));
        templates.add(createEventTemplate("День всех влюбленных", 1, 14, R.drawable.ic_event_lovers));
        templates.add(createEventTemplate("Всемирный женский день", 2, 8, R.drawable.ic_event_girl));
        templates.add(createEventTemplate("День Защитника Отечества", 1, 23, R.drawable.ic_event_deffend));
        templates.add(createEventTemplate("День свадьбы", R.drawable.ic_event_wedding));
        templates.add(createEventTemplate("День рождения ребенка", R.drawable.ic_event_baby));
        templates.add(createEventTemplate("Китайский новый год", R.drawable.ic_event_china));
        return templates;
    }

    private EventTemplate createEventTemplate(String name, int month, int day, int icon) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, month, day);
        return createEventTemplate(name, icon)
                .setDefaultDate(calendar.getTime());
    }

    private EventTemplate createEventTemplate(String name, int icon) {
        return new EventTemplate()
                .setCanModified(false)
                .setInfo(name)
                .setIdIcon(icon);
    }


    //  Persons API
    public void addPerson(Person person) {
        ContentValues insertValues = new ContentValues();
        insertValues.put("name", person.getName());
        insertValues.put("isMale", person.isMale());
        insertValues.put("photo", person.getIdPhoto());
        insertValues.put("description", person.getDescription());

        long id = db.insert("person", null, insertValues);
        person.setId((int) id);
    }

    public void updatePerson(Person person) {
        ContentValues newValues = new ContentValues();
        newValues.put("name", person.getName());
        newValues.put("isMale", person.getIdPhoto());
        newValues.put("photo", person.getName());
        newValues.put("description", person.getDescription());

        db.update("person", newValues, "idPerson=".concat(Integer.toString(person.getId())), null);
    }

    // ������ ��� ������������������� ������� � ��������������� �������
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
            Person p = new Person(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3).equalsIgnoreCase("1"), cursor.getInt(4));
            persons.add(p);
        }

        return persons;
    }

    public Person getPerson(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM person WHERE idPerson='" + id + "';", null);
        cursor.moveToNext();
        return new Person(cursor.getInt(0)
                , cursor.getString(1)
                , cursor.getString(2)
                , cursor.getString(3).equalsIgnoreCase("1")
                , cursor.getInt(4));
    }

    // Templates API
    public void addEventTemplate(EventTemplate eventTemplate) {
        ContentValues insertValues = new ContentValues();
        insertValues.put("info", eventTemplate.getInfo());
        insertValues.put("canModified", eventTemplate.isCanModified());
        if (eventTemplate.getDefaultDate() != null)
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

    // �������� ��� ������������������� ������� � ��������������� ������� �� ���
    public void deleteEventTemplate(EventTemplate eventTemplate) {
        deletePersonTemplatesByEventTemplate(eventTemplate);

        db.delete("eventTemplate", "idTemplate=".concat(Integer.toString(eventTemplate.getId())), null);
    }

    private void deleteEventsByPersonTemplate(int id) {
        db.delete("event", "idPerson=".concat(Integer.toString(id)), null);
    }

    // Óäàëÿòñÿ âñå çàïëàíèðîâàííûå ïî ïåðñîíàëüíûì øàáëîíàì ñîáûòèÿ
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

    public List<EventTemplate> getEventTemplates() {
        return  getEventTemplates(Integer.MAX_VALUE, 0);
    }

    public List<EventTemplate> getEventTemplates(int limit, int offset) {
        assert limit > 0 : "Limit must be great than 0";
        assert  offset >= 0 : "Offset must be great than 0";
        ArrayList<EventTemplate> eventTemplates = new ArrayList<EventTemplate>();

        Cursor cursor = db.rawQuery("SELECT * FROM eventTemplate LIMIT " + limit + " OFFSET " + offset, null);

        while(cursor.moveToNext()) {
            EventTemplate et = new EventTemplate(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2).equalsIgnoreCase("1"), new Date(cursor.getLong(3)), cursor.getInt(4));
            eventTemplates.add(et);
        }

        return eventTemplates;
    }

    public EventTemplate getEventTemplate(int id) {
        if (id < 0)
            return null;
         Cursor cursor = db.rawQuery("SELECT * FROM eventTemplate WHERE idTemplate='" + id + "'", null);
        cursor.moveToNext();
        return new EventTemplate(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2).equalsIgnoreCase("1"), new Date(cursor.getLong(3)), cursor.getInt(4));
    }

    // PersonTemplate API

    public void addPersonTemplate(PersonTemplate personTemplate) {
        ContentValues insertValues = new ContentValues();
        insertValues.put("idPerson", personTemplate.getPerson().getId());
        insertValues.put("idTemplate", personTemplate.getEventTemplate() == null ? -1 : personTemplate.getEventTemplate().getId());
        insertValues.put("customDate", personTemplate.getCustomDate().getTime());
        insertValues.put("cooldown", personTemplate.getCooldown().getTime());
        insertValues.put("remindTime", personTemplate.getReminderTime());
        insertValues.put("enabled", personTemplate.isEnabled());
        if (personTemplate.getInfo() != null)
            insertValues.put("info", personTemplate.getInfo());
        else {
            insertValues.put("info", "");
        }

        long id = db.insert("personTemplate", null, insertValues);
        personTemplate.setId((int) id);
    }

    public void updatePersonTemplate(PersonTemplate personTemplate) {
        ContentValues newValues = new ContentValues();
        newValues.put("idPerson", personTemplate.getPerson().getId());
        newValues.put("idTemplate", personTemplate.getEventTemplate() == null ? -1 : personTemplate.getEventTemplate().getId());
        newValues.put("customDate", personTemplate.getCustomDate().getTime());
        newValues.put("cooldown", personTemplate.getCooldown().getTime());
        newValues.put("remindTime", personTemplate.getReminderTime());
        newValues.put("enabled", personTemplate.isEnabled());
        if (personTemplate.getInfo() != null)
            newValues.put("info", personTemplate.getInfo());
        else {
            newValues.put("info", "");
        }

        db.update("personTemplate", newValues, "idPersonTemplate=".concat(Integer.toString(personTemplate.getId())), null);
    }

    // Óäàëÿòñÿ âñå çàïëàíèðîâàííûå ïî ïåðñîíàëüíîìó øàáëîíó ñîáûòèÿ
    public void deletePersonTemplate(int id) {
        deleteEventsByPersonTemplate(id);
        db.delete("personTemplate", "idPersonTemplate=" + id, null);
    }

    public PersonTemplate getPersonTemplate(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM personTemplate WHERE idPersonTemplate='" + id + "'", null);
        if (!cursor.moveToNext())
            return null;
        Person person = getPerson(cursor.getInt(1));
        EventTemplate eventTemplate = null;
        if (!cursor.isNull(2))
            eventTemplate = getEventTemplate(cursor.getInt(2));

        return new PersonTemplate(cursor.getInt(0), person,
                eventTemplate, new Date(cursor.getLong(3)), new Date(cursor.getLong(4)), cursor.getLong(5), cursor.getString(6).equalsIgnoreCase("1"), cursor.getString(7));
    }

    public ArrayList<PersonTemplate> getPersonTemplatesByPerson(int id, int limit, int offset) {
        assert limit > 0 : "Limit must be great than 0";
        assert  offset >= 0 : "Offset must be great than 0";
        Cursor cursor = db.rawQuery("SELECT * FROM personTemplate WHERE idPerson="
                + id + " LIMIT " + limit + " OFFSET " + offset, null);
        return getPersonTemplates(cursor);
    }

    public ArrayList<PersonTemplate> getPersonTemplates(int limit, int offset) {
        assert limit > 0 : "Limit must be great than 0";
        assert  offset >= 0 : "Offset must be great than 0";
        Cursor cursor = db.rawQuery("SELECT * FROM personTemplate LIMIT " + limit + " OFFSET " + offset, null);
        return getPersonTemplates(cursor);
    }


    private ArrayList<PersonTemplate> getPersonTemplates(Cursor cursor){
        ArrayList<PersonTemplate> personTemplates = new ArrayList<PersonTemplate>();
        while(cursor.moveToNext()) {
            Person person = getPerson(cursor.getInt(1));
            EventTemplate eventTemplate = null;
            if (!cursor.isNull(2))
                eventTemplate = getEventTemplate(cursor.getInt(2));

            PersonTemplate pt = new PersonTemplate(cursor.getInt(0), person,
                    eventTemplate, new Date(cursor.getLong(3)), new Date(cursor.getLong(4)), cursor.getLong(5), cursor.getString(6).equalsIgnoreCase("1"), cursor.getString(7));
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
        insertValues.put("status", event.getStatus().getId());

        long id = db.insert("event", null, insertValues);
        event.setId((int) id);
    }

    public void updateEvent(Event event) {
        ContentValues newValues = new ContentValues();
        newValues.put("idPerson", event.getPerson().getId());
        newValues.put("idPersonTemplate", event.getPersonTemplate().getId());
        newValues.put("info", event.getInfo());
        newValues.put("date", event.getDate().getTime());
        newValues.put("status", event.getStatus().getId());

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

    public Event getLastEventByPersonTemplate(Integer id) {

        Cursor cursor = db.rawQuery("SELECT * FROM event WHERE idPersonTemplate='" + id + "' ORDER BY date DESC;", null);
        if (cursor.moveToNext()) {
            Person person = getPerson(cursor.getInt(1));
            PersonTemplate personTemplate = getPersonTemplate(cursor.getInt(2));

            return new Event(cursor.getInt(0), person,
                    personTemplate, cursor.getString(3), new Date(cursor.getLong(4)), Status.fromInteger(cursor.getInt(5)));
        }
        else
            return null;
    }

    public static AppDb getInstance(FriendEdit conetext) {
        return new AppDb(conetext);
    }

    public Date getLastAchievedEventDateByPerson(Integer id) {
        Cursor cursor = db.rawQuery("SELECT date FROM event WHERE idPerson='" + id + "' AND status=" + Status.ACHIEVED.getId() + " ORDER BY date DESC;", null);
        if (cursor.moveToNext())
            return new Date(cursor.getLong(1));
        return new Date(0);
    }
    public List<PersonTemplate> getPersonTemplates(Person person) {
        return getPersonTemplatesByPerson(person.getId(), Integer.MAX_VALUE, 0);
    }
}