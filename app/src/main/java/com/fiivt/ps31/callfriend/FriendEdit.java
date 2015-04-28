package com.fiivt.ps31.callfriend;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class FriendEdit extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_edit);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ActionBar actionBar = getActionBar();
//эти ребята для ширины экрана
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.a_bar_friend_edit, null);
        mCustomView.setMinimumWidth(outMetrics.widthPixels);//при выставлении минимального экрана по дисплею показывает норм,
        //но это ультракостыль.  Поэтому было бы не плохо сохранить исходное форматирование. или нет.
        ImageButton imageButton = (ImageButton) mCustomView.findViewById(R.id.imgButtonDiscard);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Refresh Clicked!",
                        Toast.LENGTH_LONG).show();
            }
        });

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xff05a8f5));
        getMenuInflater().inflate(R.menu.menu_friend_edit, menu);

        return super.onCreateOptionsMenu(menu);
       // return true;
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
    public void onSaveContactClick(View view)
    {
        // выводим сообщение
        TextView ji = (TextView)findViewById(R.id.fe_textViewEvents);
        ji.setText("dfdd");
        //Toast.makeText(this, "Зачем вы нажали?", Toast.LENGTH_SHORT).show();
    }

    public void onDiscardContactClick(View view)
    {
        // выводим сообщение
        Toast.makeText(this, "Зачем вы dd?", Toast.LENGTH_SHORT).show();
    }
}
