package com.fiivt.ps31.callfriend;

import lombok.Data;

/**
 * Created by YGV on 11.05.2015.
 */
@Data
public class NavDrawerItem {
    private String title;
    private int icon;
    private int color;

    public NavDrawerItem() {
    }

    public NavDrawerItem(String title, int icon, int color) {
        this.title = title;
        this.icon = icon;
        this.color = color;
    }

    public NavDrawerItem(String title) {
        this.title = title;
    }
}