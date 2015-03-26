package com.fiivt.ps31.callfriend.AppDatabase;

import java.util.Date;

import lombok.Data;

/**
 * Created by Egor on 24.03.2015.
 */
@Data
public class Person {
    private Integer id;
    private String name;
    private Date dob;
    private  boolean isMale;

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

}
