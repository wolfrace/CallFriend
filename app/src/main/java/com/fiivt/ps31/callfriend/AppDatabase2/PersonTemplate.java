package com.fiivt.ps31.callfriend.AppDatabase2;

import com.fiivt.ps31.callfriend.Utils.Status;
import lombok.Data;

import java.util.Date;

/**
 * Created by Egor on 23.04.2015.
 */
@Data
public class PersonTemplate {
    private Integer id;
    private Person person;
    private EventTemplate eventTemplate;
    private Date customDate;
    private Date cooldown;

    public PersonTemplate(Integer id, Person person, EventTemplate eventTemplate, Date customDate, Date cooldown) {
        this.id = id;
        this.person = person;
        this.eventTemplate =eventTemplate;
        this.customDate = customDate;
        this.cooldown = cooldown;
    }

    public PersonTemplate(Person person, EventTemplate eventTemplate, Date customDate, Date cooldown) {
        this.id = 0;
        this.person = person;
        this.eventTemplate = eventTemplate;
        this.customDate = customDate;
        this.cooldown = cooldown;
    }

    private String generateInfo(String personInfo, String templateInfo) {
        return personInfo.concat(". ").concat(templateInfo);
    }

    // TODO
    private Date generateDate() {
        return new Date(customDate.getTime() + cooldown.getTime());
    }

    public Event generateEvent() {
        Event event = new Event(person.getId(),
                id,
                generateInfo(person.getName(), eventTemplate.getInfo()),
                generateDate(),
                Status.EXPECTED
        );
        return event;
    }
}
