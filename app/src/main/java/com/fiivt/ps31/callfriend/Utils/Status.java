package com.fiivt.ps31.callfriend.Utils;

/**
 * Created by Egor on 23.04.2015.
 */

import lombok.Getter;

@Getter
public enum Status {
    EXPECTED(0),
    ACHIEVED(1),
    DELETED(2);

    private int id;

    Status(int id){
        this.id = id;
    }

    public static Status fromInteger(int x) {
        for(Status status: values()){
            if (status.id == x){
                return status;
            }
        }
        return null;
    }
}
