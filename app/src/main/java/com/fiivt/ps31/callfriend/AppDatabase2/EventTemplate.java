package com.fiivt.ps31.callfriend.AppDatabase2;

/**
 * Created by Egor on 23.04.2015.
 */
import lombok.Data;

import java.util.Date;

@Data
public class EventTemplate {
    private Integer id;
    private String info;
    private Date cooldown;

    public EventTemplate(Integer id, String info, Date cooldown) {
        this.id = id;
        this.info = info;
        this.cooldown = cooldown;
    }

    public EventTemplate(String info, Date cooldown) {
        this.id = 0;
        this.info = info;
        this.cooldown = cooldown;
    }

    // TODO
    private String generateInfo(String parentInfo, String personInfo) {
        return parentInfo.concat(" ").concat(personInfo);
    }

    private Date generateDate(Date cooldown, Date lastEventDate) {
        return new Date(lastEventDate.getTime() + cooldown.getTime());
    }

    public Event generateEvent(Person person) {

        Event event = new Event(id,
                person.getId(),
                generateInfo(info, person.getInfo()),
                generateDate(cooldown, person.getLastEventDate(id))
        );
        return event;
    }

}
