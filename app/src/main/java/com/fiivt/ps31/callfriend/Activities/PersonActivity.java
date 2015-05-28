package com.fiivt.ps31.callfriend.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.BaseActivity;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.FriendLastActive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;
import lombok.Data;

/**
 * Created by Äàíèë on 24.04.2015.
 */
public class PersonActivity extends BaseActivity{

    public AppDb database;
    private View friendListEmptyNotify;
    private  ArrayAdapter personAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new AppDb(this);
        setContentView(R.layout.person_list_layout);


        friendListEmptyNotify = findViewById(R.id.friends_not_existing_notify);
        CircleButton addPersonButton = (CircleButton)findViewById(R.id.person_add_image);
        Button addFriendsButton = (Button) findViewById(R.id.add_friends_button);

        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonActivity.this, FriendEdit.class);
                startActivity(intent);
            }
        });


        ListView personsListView = (ListView) findViewById(R.id.person_list_view);
        List<Person> person = database.getPersons(100, 0);
        personAdapter = new PersonArrayAdapter(this, person);


        person = sortPersons(person);

        ArrayAdapter personAdapter = new PersonArrayAdapter(this, person);
        personsListView.setAdapter(personAdapter);

        personsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Person person = ((PersonArrayAdapter) adapterView.getAdapter()).getItem(pos);
                Intent intent = new Intent(PersonActivity.this, PersonProfileActivity.class);
                intent.putExtra("person", person);
                startActivity(intent);
            }
        });
        notifyOnFriendListChanged();
    }

    private void notifyOnFriendListChanged() {
        boolean isEmpty = personAdapter.isEmpty();
        friendListEmptyNotify.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    public boolean isEmpty() {
        return personAdapter.isEmpty();
    }

    List<Person> sortPersons(List<Person> persons){
        List<Person> p30 = new ArrayList<Person>();
        List<Person> p60 = new ArrayList<Person>();
        List<Person> pMore = new ArrayList<Person>();

        for (Person p: persons){
            long timeLeft = System.currentTimeMillis() - database.getLastAchievedEventDateByPerson(p.getId()).getTime();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(timeLeft);

            if(daysLeft >= 60){
                p.setActiveStatus(FriendLastActive.INFINITY);
                pMore.add(p);
            }
            else if (daysLeft < 60 && daysLeft >= 30){
                p.setActiveStatus(FriendLastActive.TWO_MONTH);
                p60.add(p);
            }
            else{
                p.setActiveStatus(FriendLastActive.MONTH);
                p30.add(p);
            }
        }
        pMore.addAll(p60);
        pMore.addAll(p30);
        return pMore;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_persons_list, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Data
     class PersonViewHolder {
        private TextView name;
        private ImageView image;
        private TextView personNote;
        private TextView personStatus;
        private RelativeLayout personStatusRL;

        public void setPersonValues(Person person) {

           /* long timeLeft = System.currentTimeMillis() - database.getLastAchievedEventDateByPerson(person.getId()).getTime();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(timeLeft);*/

            if(person.getActiveStatus() == FriendLastActive.INFINITY){
                personStatus.setText(R.string.status_red_txt);
                personStatusRL.setBackgroundResource(R.drawable.red_label);
            }
            else if (person.getActiveStatus() == FriendLastActive.TWO_MONTH){
                personStatus.setText(R.string.status_yellow_txt);
                personStatusRL.setBackgroundResource(R.drawable.yellow_label);
            }
            else{
                personStatus.setText(R.string.status_green_txt);
                personStatusRL.setBackgroundResource(R.drawable.green_label);
            }

            name.setText(person.getName());
            personNote.setText(person.getDescription());

            String mPhotoPath = person.getIdPhoto();
            if (mPhotoPath != "") {
                try {
                    image.setImageBitmap(BitmapFactory.decodeFile(mPhotoPath));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
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
            holder.setImage((ImageView) view.findViewById(R.id.profile_image));
            holder.setPersonNote((TextView) view.findViewById(R.id.person_note));
            holder.setPersonStatus((TextView) view.findViewById(R.id.person_ststus));
            holder.setPersonStatusRL((RelativeLayout) view.findViewById(R.id.person_status_rl));
            view.setTag(holder);
            return holder;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Person getItem(int pos) {
            return values.get(pos);
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
