package com.fiivt.ps31.callfriend.Utils;

/**
 * Created by Egor on 23.04.2015.
 */
public enum Status {
    EXPECTED,
    ACHIEVED;

    public static Status fromInteger(int x) {
        switch(x) {
            case 0:
                return EXPECTED;
            case 1:
                return ACHIEVED;
        }
        return null;
    }

    public static Integer toInteger(Status x) {
        switch(x) {
            case EXPECTED:
                return 0;
            case ACHIEVED:
                return 1;
        }
        return null;
    }
}
