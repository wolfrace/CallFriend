package com.fiivt.ps31.callfriend.AppDatabase;

import com.fiivt.ps31.callfriend.Utils.FriendLastActive;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Egor on 24.03.2015.
 */

public class Person implements Serializable{

    private Integer id;
    private String name;
    private String description;
    private  boolean isMale;
    private String idPhoto;
    @Setter
    @Getter
    private FriendLastActive activeStatus;

    public Person(){
        this.id = 0;
        this.name = "";
        this.description = "";
        this.idPhoto = "";
        this.isMale = false;
    }

    public Person(String name, String description, boolean isMale, String idPhoto)
    {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.idPhoto = idPhoto;
        this.isMale = isMale;
    }

    public Person(Integer id, String name, String description, boolean isMale, String idPhoto)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.idPhoto = idPhoto;
        this.isMale = isMale;
    }

   /* public void setActiveStatus(FriendLastActive la){activeStatus = la;}*/


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setIsMale(boolean isMale) {
        this.isMale = isMale;
    }

    public String getIdPhoto() {
        return idPhoto;
    }

    public void setIdPhoto(String idPhoto) {
        this.idPhoto = idPhoto;
    }
}