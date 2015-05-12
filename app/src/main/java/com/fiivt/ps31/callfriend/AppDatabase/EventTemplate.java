package com.fiivt.ps31.callfriend.AppDatabase;

/**
 * Created by Egor on 23.04.2015.
 */
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EventTemplate implements Serializable {
    private Integer id;
    private String info;
    private Date defaultDate;
    private Integer idIcon;
    private boolean canModified;


    public EventTemplate(Integer id, String info, boolean canModified, Date defaultDate, Integer idIcon) {
        this.id = id;
        this.info = info;
        this.defaultDate = defaultDate;
        this.idIcon = idIcon;
        this.canModified = canModified;
    }

    public EventTemplate(String info, boolean canModified, Date defaultDate, Integer idIcon) {
        this.id = 0;
        this.info = info;
        this.defaultDate = defaultDate;
        this.idIcon = idIcon;
        this.canModified = canModified;
    }
}
