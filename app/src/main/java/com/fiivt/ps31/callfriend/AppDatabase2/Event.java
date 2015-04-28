package com.fiivt.ps31.callfriend.AppDatabase2;

import com.fiivt.ps31.callfriend.Utils.Status;
import lombok.Data;

import java.util.Date;

/**
 * Created by Egor on 23.04.2015.
 */

@Data
public class Event {
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
        this.id = 0;
        this.personTemplate = personTemplate;
        this.person = person;
        this.info = info;
        this.date = date;
        this.status = status;
    }
}
