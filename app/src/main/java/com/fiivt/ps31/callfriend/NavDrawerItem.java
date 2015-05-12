package com.fiivt.ps31.callfriend;

import lombok.Data;

/**
 * Created by YGV on 11.05.2015.
 */
@Data
public class NavDrawerItem {
    private String title;
    private int icon;

    public NavDrawerItem() {
    }

    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title) {
        this.title = title;
    }
}