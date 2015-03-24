package com.fiivt.ps31.callfriend.AppDatabase;

import java.util.Date;

/**
 * Created by Egor on 24.03.2015.
 */
public class Person {
    private String _name;
    private Date _dob;
    private  boolean _isMale;

    public Person(String name, Date dob, boolean isMale)
    {
        _name = name;
        _dob = dob;
        _isMale = isMale;
    }

    public String getName()
    {
        return _name;
    }

    public Date getDob()
    {
        return _dob;
    }

    public boolean isMale()
    {
        return _isMale;
    }
}
