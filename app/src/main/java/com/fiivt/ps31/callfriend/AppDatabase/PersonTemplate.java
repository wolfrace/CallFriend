package com.fiivt.ps31.callfriend.AppDatabase;

import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.Status;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
    private long reminderTime;
    private boolean enabled;
    private int ZERO_YEAR = -1899;

    public PersonTemplate(Integer id
            , Person person
            , EventTemplate eventTemplate
            , Date customDate
            , Date cooldown
            , long reminderTime
            , boolean enabled
            , String info) {
        this.id = id;
        this.person = person;
        this.eventTemplate = eventTemplate;
        this.customDate = customDate;
        this.cooldown = cooldown;
        this.reminderTime = reminderTime;
        if (info == null) {
            this.info = eventTemplate.getInfo();
        }
        else{
            this.info = info;
        }
        this.enabled = enabled;
    }

    public PersonTemplate(Person person
            , EventTemplate eventTemplate
            , Date customDate
            , Date cooldown
            , long reminderTime
            , boolean enabled
            , String info) {
        this(0, person, eventTemplate, customDate, cooldown, reminderTime, enabled, info);
    }

    public PersonTemplate(Integer id
            , Person person
            , String name
            , Date customDate
            , Date cooldown
            , long reminderTime
            , boolean enabled) {
        this.id = id;
        this.info = name;
        this.person = person;
        this.cooldown = cooldown;
        this.customDate = customDate;
        this.reminderTime = reminderTime;
        this.enabled = enabled;
    }

    private String generateInfo(String personInfo, String templateInfo) {
        return personInfo.concat(". ").concat(templateInfo);
    }

    // TODO
    private Date generateDate(Date lastDate) {
        return applyCooldown(lastDate);//new Date(lastDate.getTime() + cooldown.getTime());
    }

    public Event generateEvent(Date lastDate) {
        return new Event(person,
                this,
                (eventTemplate == null) ? null : eventTemplate.getInfo(),//generateInfo(person.getName(), eventTemplate == null ? "null" : eventTemplate.getInfo()),
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

    public String getCustomDateString(){
        String pattern = "d MMMM yyyy 'г.'";
        if (ZERO_YEAR == customDate.getYear())
            pattern = "d MMMM";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("ru", "RU"));
        return sdf.format(customDate);
    }

    public Date getCooldown(){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(customDate);
        return gc.isLeapYear(gc.get(Calendar.YEAR)) ?
                new Date(TimeUnit.DAYS.toMillis(366)) :
                new Date(TimeUnit.DAYS.toMillis(365));
    }

    public Date applyCooldown(Date date){//year cd
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        return new GregorianCalendar(gc.get(Calendar.YEAR) + 1, gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH)).getTime();
    }

    public void setTitle(String name) {
        this.info = name;
    }
}
