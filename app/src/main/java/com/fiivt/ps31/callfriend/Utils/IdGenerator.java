package com.fiivt.ps31.callfriend.Utils;

public class IdGenerator {

    private static int previousId = 0;

    public synchronized static int generate() {
        return --previousId;
    }
}
