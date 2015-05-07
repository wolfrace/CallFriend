package com.fiivt.ps31.callfriend.AppDatabase;

import lombok.Data;

/**
 * Created by Egor on 24.03.2015.
 */
@Data
public class Person {

    private Integer id;
    private String name;
    private  boolean isMale;
    private int idPhoto;

    public Person(String name, boolean isMale, int idPhoto)
    {
        this.id = 0;
        this.name = name;
        this.idPhoto = idPhoto;
        this.isMale = isMale;
    }

    public Person(Integer id, String name, boolean isMale, int idPhoto)
    {
        this.id = id;
        this.name = name;
        this.idPhoto = idPhoto;
        this.isMale = isMale;
    }
}