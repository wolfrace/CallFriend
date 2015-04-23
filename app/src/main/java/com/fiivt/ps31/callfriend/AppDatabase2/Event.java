package com.fiivt.ps31.callfriend.AppDatabase2;

import lombok.Data;

import java.util.Date;

/**
 * Created by Egor on 23.04.2015.
 */

@Data
public class Event {
    private Integer id;
    private Integer idTemplate;
    private Integer idPerson;
    private String info;
    private Date date;

    public Event(Integer id, Integer idTemplate, Integer idPerson, String info, Date date) {
        this.id = id;
        this.idTemplate = idTemplate;
        this.idPerson = idPerson;
        this.info = info;
        this.date = date;
    }

    public Event(Integer idTemplate, Integer idPerson, String info, Date date) {
        this.id = 0;
        this.idTemplate = idTemplate;
        this.idPerson = idPerson;
        this.info = info;
        this.date = date;
    }
}
