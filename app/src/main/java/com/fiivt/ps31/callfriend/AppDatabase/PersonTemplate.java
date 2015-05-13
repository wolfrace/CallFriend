package com.fiivt.ps31.callfriend.AppDatabase;

import com.fiivt.ps31.callfriend.Utils.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Egor on 23.04.2015.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PersonTemplate implements Serializable {
    private Integer id;
    private Person person;
    private EventTemplate eventTemplate;
    private Date customDate;
    private Date cooldown;
    private int remindTime;
    private boolean enabled;

    public PersonTemplate(Integer id, Person person, EventTemplate eventTemplate, Date customDate, Date cooldown, int remindTime, boolean enabled) {
        this.id = id;
        this.person = person;
        this.eventTemplate =eventTemplate;
        this.customDate = customDate;
        this.cooldown = cooldown;
        this.remindTime = remindTime;
        this.enabled = enabled;
    }

    public PersonTemplate(Person person, EventTemplate eventTemplate, Date customDate, Date cooldown, int remindTime, boolean enabled) {
        this.id = 0;
        this.person = person;
        this.eventTemplate = eventTemplate;
        this.customDate = customDate;
        this.cooldown = cooldown;
        this.remindTime = remindTime;
        this.enabled = enabled;
    }

    private String generateInfo(String personInfo, String templateInfo) {
        return personInfo.concat(". ").concat(templateInfo);
    }

    // TODO
    private Date generateDate(Date lastDate) {
        return new Date(lastDate.getTime() + cooldown.getTime());
    }

    public Event generateEvent(Date lastDate) {
        Event event = new Event(person,
                this,
                generateInfo(person.getName(), eventTemplate.getInfo()),
                generateDate(lastDate),
                Status.EXPECTED
        );
        return event;
    }
}
