package com.fiivt.ps31.callfriend.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.fiivt.ps31.callfriend.BaseActivity;
import com.fiivt.ps31.callfriend.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Switch switchPush = (Switch)findViewById(R.id.sett_switchPush);
        CompoundButton.OnCheckedChangeListener switchCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changePushSettings(b);
            }
        };
        switchPush.setOnCheckedChangeListener(switchCheckedChangeListener);
        View loadFromVkView = findViewById(R.id.sett_loadFriends);
        View.OnClickListener vkLoadClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromVK();
            }
        };
        loadFromVkView.setOnClickListener(vkLoadClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changePushSettings(boolean isChecked){
        //todo change settings
    }

    public void loadFromVK(){
        //todo load
    }
}
