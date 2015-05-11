package com.fiivt.ps31.callfriend.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import at.markushi.ui.CircleButton;
import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import android.view.Gravity;

import com.fiivt.ps31.callfriend.AppDatabase2.Person;
import com.fiivt.ps31.callfriend.R;
import lombok.Data;

import java.util.Date;
import java.util.List;

public class PersonActivity extends Activity {


    public AppDb database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new AppDb(this);
        setContentView(R.layout.person_list_layout);

        CircleButton addPersonButton = (CircleButton)findViewById(R.id.person_add_image);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //addPersonButton.setBackgroundColor();
               Intent intent = new Intent(PersonActivity.this, FriendEdit.class);
                startActivity(intent);
            }
        });
        ListView personsListView = (ListView) findViewById(R.id.person_list_view);
        final List<Person> person = database.getPersons(100, 0);
        ArrayAdapter personAdapter = new PersonArrayAdapter(this, person);
        personsListView.setAdapter(personAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        eventsListView.setOnItemClickListener(  new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//            }
//        });
        return true;
    }


    @Data
    static class PersonViewHolder {
        private TextView name;
        private ImageView image;

        public void setPersonValues(Person person) {
            name.setText(person.getName());
            //image.setImageResource(R.mipmap.ic_user);
        }
    }

    public class PersonArrayAdapter extends ArrayAdapter<Person> {
        private final Context context;
        private final List<Person> values;

        public PersonArrayAdapter(Context context, List<Person> values) {
            super(context, R.layout.row_list_cont, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getViewWithHolder(convertView, parent);
            PersonViewHolder viewHolder = (PersonViewHolder) view.getTag();
            Person person = values.get(position);
            viewHolder.setPersonValues(person);
            return view;
        }

        private View getViewWithHolder(View convertView, ViewGroup parent) {
            if (convertView == null) {
                View view = createNewView(parent);
                initializeHolder(view);
                return view;
            } else {
                return convertView;
            }
        }

        private View createNewView(ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.row_list_cont, parent, false);
        }

        private PersonViewHolder initializeHolder(View view) {
            PersonViewHolder holder = new PersonViewHolder();
            holder.setName((TextView) view.findViewById(R.id.person_list_contact_name));
            //holder.setImage((ImageView) view.findViewById(R.id.contactAvatar));
            view.setTag(holder);
            return holder;
        }

        @Override
        public int getCount() {
            return values.size();
        }
    }

    public void buttonAddOnClick(View v)
    {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context,
                "This is Toast Notification", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
