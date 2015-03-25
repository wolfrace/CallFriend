package com.fiivt.ps31.callfriend.AppDatabase;

import java.util.Date;

/**
 * Created by Egor on 25.03.2015.
 */
public class Event {
    private String _title;
    private Date _date;
    private Person _person;

    public Event(String title, Date date, Person person)
    {
        _title = title;
        _date = date;
        _person = person;
    }


}
