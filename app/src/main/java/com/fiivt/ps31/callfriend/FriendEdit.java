package com.fiivt.ps31.callfriend;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.fiivt.ps31.callfriend.AppDatabase2.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class FriendEdit extends ActionBarActivity {

    private EditText nameView;
    private CircleImageView avatarView;
    private SignificantEventAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        //todo get Person info/significant events (now only test data)
        List<SignificantEvent> events = new ArrayList<SignificantEvent>();
        for (int i = 0; i++ < 10;) {
            SignificantEvent event = new SignificantEvent();
            event.setDate(new Date(System.currentTimeMillis() + (i * 1000000)));
            event.setTitle((i % 2 == 0) ? "Best title eve " + i + " !!!" : "Short " + i);
            event.setEnabled(i % 3 == 0);
            events.add(event);
        }

        Person person = new Person("Vasya hop", false, 99999);
        // test data end
        setPersonDataOnView(person, events);
    }

    private void initView() {
        setContentView(R.layout.friend_edit_activity);
        setCustomActionBar();

        initEventsList();
        nameView = (EditText) findViewById(R.id.friend_name_edit_text);
        avatarView = (CircleImageView) findViewById(R.id.friend_avatar);
    }

    private void setPersonDataOnView(Person person, List<SignificantEvent> events) {
        // set personal info
        nameView.setText(person.getName());
        //avatarView.setImageResource(); todo set AVATAR

        // set significant events
        eventsAdapter.getValues().addAll(events);
        eventsAdapter.notifyDataSetChanged();
    }

    private void initEventsList() {
        ListView eventList = (ListView) findViewById(R.id.significant_events_list);
        eventsAdapter = new SignificantEventAdapter(getApplicationContext());
        eventList.setAdapter(eventsAdapter);

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SignificantEvent event = eventsAdapter.getItem(position);
                onEventClick(event);
            }
        });
    }

    private void onEventClick(SignificantEvent event) {
        //todo open 'select action' dialog fragment or 'edit event' dialog fragment
        //todo check ?? it's work?
    }

    @SuppressWarnings("all")
    private void setCustomActionBar() {
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.friend_edit_action_bar, null);
        setMinimalWidthAsScreenWidth(mCustomView);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);


        ImageButton saveButton = (ImageButton) mCustomView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onSave();
            }
        });

        ImageButton cancelButton = (ImageButton) mCustomView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onCancel();
            }
        });
    }

    public void onSave() {
        Person person = getPersonDataFromView();
        //todo save person
    }

    public void onCancel() {
        //todo close page
    }

    private void setMinimalWidthAsScreenWidth(View view) {
        //set width as screen size for action bar
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        view.setMinimumWidth(outMetrics.widthPixels);
    }

    public Person getPersonDataFromView() {
        //todo todo me
        return null;
    }

    @Data
    @NoArgsConstructor
    public static class SignificantEvent {
        private String title;
        //todo add event icon
        private Date date;
        private boolean enabled;
    }


    @Data
    @NoArgsConstructor
    private static class SignificantEventHolder {
        TextView title;
        CheckBox checkBox;
        CircleImageView icon;

        public void setEventValues(SignificantEvent event) {
            title.setText(event.getTitle());
            checkBox.setChecked(event.isEnabled());
            //icon.setImageResource(); todo set image
        }
    }

    public class SignificantEventAdapter extends ArrayAdapter<SignificantEvent> {
        @Getter
        private final List<SignificantEvent> values;
        private final Context context;

        public SignificantEventAdapter(Context context) {
            super(context, R.layout.row_significant_event);
            this.context = context;
            this.values = new ArrayList<SignificantEvent>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getViewWithHolder(convertView, parent);
            SignificantEventHolder viewHolder = (SignificantEventHolder) view.getTag();
            SignificantEvent event = values.get(position);
            viewHolder.setEventValues(event);
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
            return inflater.inflate(R.layout.row_significant_event, parent, false);
        }

        private SignificantEventHolder initializeHolder(View view) {
            SignificantEventHolder holder = new SignificantEventHolder();
            holder.setIcon((CircleImageView) view.findViewById(R.id.significant_event_icon));
            holder.setTitle((TextView) view.findViewById(R.id.significant_event_title));
            holder.setCheckBox((CheckBox) view.findViewById(R.id.significant_event_checkbox));
            view.setTag(holder);
            return holder;
        }

        @Override
        public int getCount() {
            return values.size();
        }

    }

}
