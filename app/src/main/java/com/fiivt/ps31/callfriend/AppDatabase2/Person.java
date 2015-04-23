package com.fiivt.ps31.callfriend.AppDatabase2;

import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by Egor on 24.03.2015.
 */
@Data
public class Person {

    private Integer id;
    private String name;
    private Date dob;
    private  boolean isMale;

    private Map<Integer, Date> idEventTemplateToLastEvent;

    public Person(String name, Date dob, boolean isMale)
    {
        this.id = 0;
        this.name = name;
        this.dob = dob;
        this.isMale = isMale;
    }

    public Person(Integer id, String name, Date dob, boolean isMale)
    {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.isMale = isMale;
    }

    public Set<Integer> getEventTemplates() {
        return idEventTemplateToLastEvent.keySet();
    }

    public boolean isContainEventTemplate(Integer idEventTemplate) {
        return  idEventTemplateToLastEvent.containsKey(idEventTemplate);
    }

    public Date getLastEventDate(Integer idEventTemplate) {
        return idEventTemplateToLastEvent.get(idEventTemplate);
    }

    public void addEventTemplate(Integer idEventTemplate) {
        idEventTemplateToLastEvent.put(idEventTemplate, new Date());
    }

    public void addEventTemplate(Integer idEventTemplate, Date lastEventDate) {
        idEventTemplateToLastEvent.put(idEventTemplate, lastEventDate);
    }

    public void deleteEventTemplate(Integer idEventTemplate) {
        idEventTemplateToLastEvent.remove(idEventTemplate);
    }

    public void updateEventTemplate(Integer idEventTemplate, Date lastEventDate) {
        idEventTemplateToLastEvent.put(idEventTemplate, lastEventDate);
    }
}