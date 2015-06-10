package com.fiivt.ps31.callfriend;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fiivt.ps31.callfriend.Activities.EventsActivity;
import com.fiivt.ps31.callfriend.Activities.PersonActivity;
import com.fiivt.ps31.callfriend.Activities.SettingsActivity;

import java.util.ArrayList;

public class BaseActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mDrawerLeft;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private Integer selectedPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedPos = getIntent().getIntExtra("selectedPos", 1);
        setContentView(R.layout.base_activity);
        moveDrawerToTop();
        initActionBar() ;

        String[] navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        TypedArray navMenuColors = getResources().obtainTypedArray(R.array.nav_drawer_div_color);
        initDrawerItems(navMenuTitles, navMenuIcons,navMenuColors );
        initDrawer();
    }

    public void initDrawerItems(String[] navMenuTitles, TypedArray navMenuIcons, TypedArray navMenuColors) {

        navDrawerItems = new ArrayList<NavDrawerItem>();

        for (int i = 0; i < navMenuTitles.length; i++) {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i]
                    ,navMenuIcons.getResourceId(i, -1)
                    ,navMenuColors.getResourceId(i, -1)));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.drawer_layout, null); // "null" is important.
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawer.findViewById(R.id.content_frame);
        container.addView(child, 0);

        View menuLayout = drawer.findViewById(R.id.left_drawer);
        menuLayout.setPadding(0, getStatusBarHeight(), 0, 0);
        menuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}// catch click on empty field
        });

        // Make the drawer replace the first child
        decor.addView(drawer);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getContentIdResource() {
        return getResources().getIdentifier("content", "id", "android");
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
        mDrawerToggle.syncState();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        ImageView iconHome = (ImageView)findViewById(android.R.id.home);
        iconHome.setPadding(0, 0, 32, 0);
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerLeft = (LinearLayout) findViewById(R.id.left_drawer);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        mDrawerLeft.setMinimumWidth(width-getStatusBarHeight());
        ImageView img = (ImageView)findViewById(R.id.drawer_back_img);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mDrawerList = (ListView)findViewById(R.id.drawer_list);
        mDrawerLayout.setDrawerListener(createDrawerToggle());
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        mDrawerList.setItemChecked(selectedPos, true);
        mDrawerList.setSelection(selectedPos);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    private DrawerLayout.DrawerListener createDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for
                // accessibility
                R.string.app_name // nav drawer close - description for
                // accessibility
        ) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int state) {
            }
        };
        return mDrawerToggle;
    }


    /**
     * Displaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
//depends on string array
        switch (position) {
            case 0://friends
                goToActivity(PersonActivity.class,position, null);
                break;
            case 1://events
                goToActivity(EventsActivity.class,position, null);
                break;
            case 2://birthday
                goToActivity(EventsActivity.class,position, "birthday");
                break;
            case 3://special
                goToActivity(EventsActivity.class,position, "special");
                break;
            case 4://settings
                goToActivity(SettingsActivity.class, position, null);
                break;
            case 5://about
                break;
            default:
                break;
        }

        // update selected item and title, then close the drawer
        mDrawerLayout.closeDrawer(mDrawerLeft);
    }

    public void goToActivity(Class activityClass , int pos, String partition){
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("selectedPos", pos);
        intent.putExtra("partition", partition);
        startActivity(intent);
        finish();// finishes the current activity
    }

    @Override
    public void setTitle(CharSequence title) {
        //mTitle = title;
        //getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    // @Override
    // protected void onPostCreate(Bundle savedInstanceState) {
    //    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    //    mDrawerToggle.syncState();
    // }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}

