package com.fiivt.ps31.callfriend.AppDatabase;

import com.fiivt.ps31.callfriend.Utils.Status;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import lombok.Data;

/**
 * Created by Egor on 23.04.2015.
 */

@Data
public class Event implements Serializable {
    private Integer id;
    private Person person;
    private PersonTemplate personTemplate;
    private String info;
    private Date date;
    private Status status;

    public Event(Integer id, Person person, PersonTemplate personTemplate, String info, Date date, Status status) {
        this.id = id;
        this.personTemplate = personTemplate;
        this.person = person;
        this.info = info;
        this.date = date;
        this.status = status;
    }

    public Event(Person person, PersonTemplate personTemplate, String info, Date date, Status status) {
        this(0, person, personTemplate, info, date, status);
    }

    public int getDaysLeft() {
        if (date == null) {
            return 0;
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        GregorianCalendar today = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));
        long timeLeft = date.getTime() - today.getTime().getTime();
        long daysLeft = TimeUnit.MILLISECONDS.toDays(timeLeft);
        return (int) daysLeft;
    }

    public void putOff(TimeUnit unit, int amount) {
        long delay = unit.toMillis(amount);
        long dateAsMillis = System.currentTimeMillis();
        if (date != null && dateAsMillis < date.getTime()) {
            dateAsMillis = date.getTime();
        }
        date = new Date(dateAsMillis + delay);
    }
}
