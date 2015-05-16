package com.fiivt.ps31.callfriend.AppDatabase2;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Egor on 24.03.2015.
 */
@Data
@NoArgsConstructor
public class Person implements Serializable {

    private int id;
    private String name;
    private String description;
    private  boolean isMale;
    private int idPhoto;

    public Person(String name, String description, boolean isMale, int idPhoto) {
        this(0, name, description, isMale, idPhoto);
    }

    public Person(Integer id, String name, String description, boolean isMale, int idPhoto) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.idPhoto = idPhoto;
        this.isMale = isMale;
    }
}