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
    private Integer idPersonTemplate;
    private Integer idPerson;
    private String info;
    private Date date;
    private Status status;

    public Event(Integer id, Integer idPerson, Integer idPersonTemplate, String info, Date date, Status status) {
        this.id = id;
        this.idPersonTemplate = idPersonTemplate;
        this.idPerson = idPerson;
        this.info = info;
        this.date = date;
        this.status = status;
    }

    public Event(Integer idPerson, Integer idPersonTemplate, String info, Date date, Status status) {
        this.id = 0;
        this.idPersonTemplate = idPersonTemplate;
        this.idPerson = idPerson;
        this.info = info;
        this.date = date;
        this.status = status;
    }
}
