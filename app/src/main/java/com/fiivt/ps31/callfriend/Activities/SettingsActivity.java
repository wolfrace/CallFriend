package com.fiivt.ps31.callfriend.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fiivt.ps31.callfriend.BaseActivity;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.Settings;

public class SettingsActivity extends BaseActivity {

    Settings settings;
    CheckBox mPushCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        settings = Settings.getInstance(this);
        mPushCheckBox = (CheckBox)findViewById(R.id.settings_push_checkbox);

        FrameLayout pushCheckboxButton = (FrameLayout)findViewById(R.id.enable_switch_push_button);
        View.OnClickListener switchCheckedChangeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePushSettings();
            }
        };
        pushCheckboxButton.setOnClickListener(switchCheckedChangeListener);
        mPushCheckBox.setChecked(settings.getIsPushEnabled());

        View loadFromVkView = findViewById(R.id.sett_loadFriends);
        View.OnClickListener vkLoadClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromVK();
            }
        };
        loadFromVkView.setOnClickListener(vkLoadClickListener);
        if (settings.isImportVkNeed());
            //disableVkButton();
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

    public void changePushSettings(){
        mPushCheckBox.setChecked(!mPushCheckBox.isChecked());
        settings.setIsPushEnabled(mPushCheckBox.isChecked());
    }

    public void loadFromVK(){
        goToActivity(VkontakteActivity.class);
        settings.setIsImportVkNeed(false);
        disableVkButton();
    }

    public void disableVkButton(){
        findViewById(R.id.sett_loadFriends).setEnabled(false);
        ((TextView)findViewById(R.id.settings_vk_label)).setTextColor(getResources().getColor(R.color.text_color_light));
    }
}
