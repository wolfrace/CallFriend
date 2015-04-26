package com.fiivt.ps31.callfriend.AppDatabase;

import android.util.TimeUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    public Event(String title, Date date, Person person) {
        this(0, title, date, person);
    }

    public Event(Integer id, String title, Date date, Person person) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.person = person;
    }

    public int getDaysLeft() {
        if (date == null) {
            return 0;
        }
        long timeLeft = date.getTime() - new Date().getTime();
        if (timeLeft > 0) {
            long daysLeft = TimeUnit.MILLISECONDS.toDays(timeLeft);
            return (int) daysLeft;
        } else {
            return 0;
        }
    }
}
