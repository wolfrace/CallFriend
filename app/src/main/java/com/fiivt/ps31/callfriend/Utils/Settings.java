package com.fiivt.ps31.callfriend.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Egor on 10.05.2015.
 */
public class Settings
{
    public static final String APP_PREFERENCES = "callFriend_settings";
    public static final String APP_PREFERENCES_IMPORT_VK = "IMPORT_VK_NEED";
    public static final String APP_PREFERENCES_PUSH_ENABLED = "PUSH_ENABLED";

    private SharedPreferences preferences;
    private boolean isImportVk;
    private boolean isPushEnabled;

    private static Settings instance;
    private Settings(Context c) {
        preferences = c.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (preferences.contains(APP_PREFERENCES_IMPORT_VK)) {
            isImportVk = preferences.getBoolean(APP_PREFERENCES_IMPORT_VK, true);
        }
        else {
            isImportVk = true;
        }

        if (preferences.contains(APP_PREFERENCES_PUSH_ENABLED)) {
            isPushEnabled = preferences.getBoolean(APP_PREFERENCES_PUSH_ENABLED, true);
        }
        else {
            isPushEnabled = true;
        }
    }

    public boolean isImportVkNeed() {
        return isImportVk;
    }

    public void setIsImportVkNeed(boolean val) {
        if (val != isImportVk) {
            isImportVk = val;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(APP_PREFERENCES_IMPORT_VK, isImportVk);
            editor.apply();
        }
    }

    public boolean getIsPushEnabled() {
        return isPushEnabled;
    }

    public void setIsPushEnabled(boolean val) {
        if (val != isPushEnabled) {
            isPushEnabled = val;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(APP_PREFERENCES_PUSH_ENABLED, isPushEnabled);
            editor.apply();
        }
    }

    public static Settings getInstance(Context c)
    {
        if (instance == null)
        {
            instance = new Settings(c);
        }
        return instance;
    }
}