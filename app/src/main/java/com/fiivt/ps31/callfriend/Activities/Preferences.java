package com.fiivt.ps31.callfriend.Activities;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.fiivt.ps31.callfriend.R;

// http://developer.android.com/reference/android/preference/PreferenceActivity.html

/*
how to use:
SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
String strUserName = SP.getString("username", "NA");
boolean bAppUpdates = SP.getBoolean("applicationUpdates",false);
String downloadType = SP.getString("downloadType","1");
*/

// http://startandroid.ru/ru/uroki/vse-uroki-spiskom/137-urok-74-preferences-programmnoe-sozdanie-ekrana-nastroek

public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.button_import_vk, null, false);

        Button login = (Button) footerView.findViewById(R.id.buttonImportVk);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImportVkButton(v);
            }
        });
    }

    private void onClickImportVkButton(View v) {
        int a = 14;
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}