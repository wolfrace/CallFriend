package com.fiivt.ps31.callfriend.AppDatabase;

import java.util.Date;

import lombok.Data;

/**
 * Created by Egor on 25.03.2015.
 */
@Data
public class Event {
    private Integer id;
    private String title;
    private Date date;
    private Person person;

    public Event(String title, Date date, Person person)
    {
        this.id = 0;
        this.title = title;
        this.date = date;
        this.person = person;
    }

    public Event(Integer id, String title, Date date, Person person)
    {
        this.id = id;
        this.title = title;
        this.date = date;
        this.person = person;
    }

}
