package com.fiivt.ps31.callfriend.AppDatabase;

import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.Status;
import lombok.Data;

import java.util.Date;

/**
 * Created by Egor on 23.04.2015.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PersonTemplate implements Serializable {

    private static final int CUSTOM_EVENT_ICON = R.drawable.ic_event_special;

    private Integer id;
    private Person person;
    //todo FIX ME FAST save into db info field
    //todo remove EventTemplate link from PersonTemplate

    private String info;
    private EventTemplate eventTemplate;
    private Date customDate;
    private Date cooldown;
    private int remindTime;

    public PersonTemplate(Integer id, Person person, EventTemplate eventTemplate, Date customDate, Date cooldown, long reminderTime, boolean enabled) {
        this.id = id;
        this.person = person;
        this.eventTemplate = eventTemplate;
        this.customDate = customDate;
        this.cooldown = cooldown;
        this.reminderTime = reminderTime;
        if (eventTemplate != null) {
            this.info = eventTemplate.getInfo();
        }
    }

    public PersonTemplate(Person person, EventTemplate eventTemplate, Date customDate, Date cooldown, long reminderTime, boolean enabled) {
        this(0, person, eventTemplate, customDate, cooldown, reminderTime, enabled);
    }

    public PersonTemplate(int id, Person person, String name, Date customDate, long reminderTime) {
        this.id = id;
        this.info = name;
        this.person = person;
        this.customDate = customDate;
        this.remindTime = remindTime;
    }

    private String generateInfo(String personInfo, String templateInfo) {
        return personInfo.concat(". ").concat(templateInfo);
    }

    // TODO
    private Date generateDate() {
        return new Date(customDate.getTime() + cooldown.getTime());
    }

    public Event generateEvent(Date lastDate) {
        return new Event(person,
                this,
                generateInfo(person.getName(), eventTemplate.getInfo()),
                generateDate(lastDate),
                Status.EXPECTED
        );
    }

    public String getTitle() {
        return getInfo();
    }

    public int getIconResId() {
        int iconId = eventTemplate == null ? 0 : eventTemplate.getIdIcon();
        if (iconId <= 0) {
            iconId = CUSTOM_EVENT_ICON;
        }
        return iconId;
    }

    public void setTitle(String name) {
        this.info = name;
    }
}
