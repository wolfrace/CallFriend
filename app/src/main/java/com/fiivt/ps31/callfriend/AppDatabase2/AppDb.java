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
        // ������� ����� ���, ���, ����������
        db.execSQL("CREATE TABLE IF NOT EXISTS person(idPerson INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, name VARCHAR, isMale BOOLEAN, photo BLOB);");

        // ������ ������� ����� ���������� � �������, ���� �� ���������, ������������� ������ � ����, ����������� � ����������� ������ ���� � ������� ������� (��� ������ ����, ��������, ������)
        db.execSQL("CREATE TABLE IF NOT EXISTS eventTemplate(idTemplate INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, info VARCHAR, canModified BOOLEAN, defaultDate DATE, idIcon INTEGER);");

        // ������������ ������ ������ ����������� ��� ����������� �������� ������ �������. �������� ������������� �������, ������� �������, ���� ������ � �������
        db.execSQL("CREATE TABLE IF NOT EXISTS personTemplates(idPersonTemplate INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, idPerson INTEGER, idTemplate INTEGER, customDate DATE, cooldown DATE);");

        // ������� ������ �������������� �������, ������������� �������, ���� ������� � ������ �������
        db.execSQL("CREATE TABLE IF NOT EXISTS event(idEvent INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, idPerson INTEGER, idPersonTemplate INTEGER, info VARCHAR, date DATE, status INTEGER);");
    }


    //  Persons API
    public void addPerson(Person person) {

    }

    public void updatePerson(Person person) {

    }

    // ������ ��� ������������������� ������� � ��������������� �������
    public void deletePerson(Person person) {

    }

    public List<Person> getPersons() {
        return null;
    }

    public Person getPerson() {
        return null;
    }

    // Templates API
    public void AddEventTemplate(EventTemplate eventTemplate) {

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