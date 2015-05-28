package com.fiivt.ps31.callfriend.Activities;

import android.app.Activity;
import android.os.Bundle;
import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.R;
import lombok.Data;

/**
 * Created by Данил on 28.05.2015.
 */
public class PersonProfileActivity extends Activity {

    public AppDb database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new AppDb(this);
        setContentView(R.layout.person_profile_view);
    }
}
