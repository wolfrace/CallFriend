package com.fiivt.ps31.callfriend.AppDatabase;

import java.util.Date;

import lombok.Data;

/**
 * Created by Egor on 24.03.2015.
 */
@Data
public class Person {
    private String name;
    private Date dob;
    private  boolean isMale;

    public Person(String name, Date dob, boolean isMale)
    {
        this.name = name;
        this.dob = dob;
        this.isMale = isMale;
    }

}
