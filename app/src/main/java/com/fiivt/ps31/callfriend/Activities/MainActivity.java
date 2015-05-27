package com.fiivt.ps31.callfriend.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import com.fiivt.ps31.callfriend.BaseActivity;
import com.fiivt.ps31.callfriend.CustomPagerAdapter;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Service.Alarm;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends Activity {
    Alarm alarm = new Alarm();
    Button skipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        chbDefaultCircleIndicatorSnap = (CheckBox) findViewById(R.id.chbDefaultCircleIndicatorSnap);
//        chbDefaultCircleIndicatorSnap.setOnCheckedChangeListener(onCheckedChange);

        ViewPager viewPager = (ViewPager)findViewById(R.id.pager);

        CustomPagerAdapter viewPagerAdapter = new CustomPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        CirclePageIndicator circleIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        circleIndicator.setViewPager(viewPager);

        skipBtn = (Button)findViewById(R.id.buttonSkip);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startApplication();
            }
        });


    }

    private void startApplication(){
        alarm.SetAlarm(this);
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
