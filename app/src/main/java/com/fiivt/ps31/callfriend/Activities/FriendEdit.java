package com.fiivt.ps31.callfriend.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.SignificantEventActionDialog;
import com.fiivt.ps31.callfriend.SignificantEventEditDialog;
import com.fiivt.ps31.callfriend.SignificantEventEditDialog.OnDataSetChangedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Service.EventService;
import com.fiivt.ps31.callfriend.Utils.ExpandedListView;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class FriendEdit extends Activity implements OnDataSetChangedListener {

    private static final int INVALID_EVENT_ID = -1;
    private EditText nameView;
    private EditText descriptionView;
    private CircleImageView avatarView;
    private Integer hiddenPersonId;
    private SignificantEventAdapter eventsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //test
        startService(new Intent(this, EventService.class));

        super.onCreate(savedInstanceState);
        initView();

        int iconsResIds[] = {
                R.drawable.ic_event_birthday, R.drawable.ic_event_girl,
                R.drawable.ic_event_baby, R.drawable.ic_event_deffend,
                R.drawable.ic_event_angel, R.drawable.ic_event_wedding,
                R.drawable.ic_event_lovers, R.drawable.ic_event_xmass,
                R.drawable.ic_event_china, R.drawable.ic_event_special
        };

        //todo get Person info/significant events (now only test data)
        List<SignificantEvent> events = new ArrayList<SignificantEvent>();
        for (int i = 0; i++ < 10;) {
            SignificantEvent event = new SignificantEvent();
            event.setId(i);
            event.setModified(i % 2 == 0);
            event.setDate(new Date(System.currentTimeMillis() + (i * 1000000)));
            event.setTitle((i % 2 == 0) ? "Best title eve " + i + " !!!" : "Short " + i);
            event.setEnabled(i % 3 == 0);
            event.setIconResId(iconsResIds[i % iconsResIds.length]);
            event.setReminderTime(TimeUnit.DAYS.toMillis(7));
            events.add(event);
        }



        Person person = new Person("Vasya hop", "So svadbi Leni", false, 99999);
        // test data end
        setPersonDataOnView(person, events);
    }

    private void initView() {
        setContentView(R.layout.friend_edit_activity);
        setCustomActionBar();

        initButtons();
        initEventsList();
        nameView = (EditText) findViewById(R.id.friend_name_edit_text);
        descriptionView = (EditText) findViewById(R.id.friend_description_edit_text);
        avatarView = (CircleImageView) findViewById(R.id.friend_avatar);
    }

    private void initButtons() {
        View createNewSignificantEventButton = findViewById(R.id.add_significant_event_button);
        createNewSignificantEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateNewSignificantEvent();
            }
        });

        View changeAvatarButton = findViewById(R.id.change_avatar_button);
        changeAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeAvatar();
            }
        });
    }

    private void setPersonDataOnView(Person person, List<SignificantEvent> events) {
        // set personal info
        nameView.setText(person.getName());
        nameView.setText(person.getDescription());
        hiddenPersonId = person.getId();
        //avatarView.setImageResource(); todo set AVATAR

        // set significant events
        eventsAdapter.getValues().addAll(events);
        eventsAdapter.notifyDataSetChanged();
    }

    private void initEventsList() {
        ExpandedListView eventList = (ExpandedListView) findViewById(R.id.significant_events_list);
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SignificantEvent event = eventsAdapter.getItem(position);
                if (event.isModified()) {
                    onEventEditClick(event);
                } else {
                    onSignificantEventCheckBoxClick(event);
                }
            }
        });
        eventsAdapter = new SignificantEventAdapter(getApplicationContext());
        eventList.setAdapter(eventsAdapter);
    }

    private void onChangeAvatar() {
        //todo set avatar ???
    }

    private void onCreateNewSignificantEvent() {
        // todo create new significant date
        showSignificantEventEditDialog(null);
    }

    private void onDeleteSignificantEvent(SignificantEvent event) {
        //todo delete from db ??
        eventsAdapter.deleteEvent(event);
    }

    private void onSignificantEventEdit(SignificantEvent event) {
        showSignificantEventEditDialog(event);
    }

    private void onSignificantEventCheckBoxClick(SignificantEvent event) {
        boolean isInitialized = event.getDate() != null;
        if (isInitialized) {
            event.setEnabled(!event.isEnabled());
            eventsAdapter.notifyDataSetChanged();
        } else {
            event.setEnabled(true);
            showSignificantEventEditDialog(event);
        }
    }

    private void showSignificantEventEditDialog(SignificantEvent event) {
        FragmentManager manager = getFragmentManager();
        SignificantEventEditDialog dialog = new SignificantEventEditDialog();

        Bundle args = new Bundle();
        if (event != null){
            args.putInt("id", event.getId());
            args.putInt("iconResId", event.getIconResId());
            args.putString("eventName", event.getTitle());
            args.putSerializable("eventDate", event.getDate());
            args.putLong("reminderTime", event.getReminderTime());
        } else {
            args.putInt("id", INVALID_EVENT_ID);
        }

        dialog.setArguments(args);
        dialog.setListener(this);
        dialog.show(manager, "sgnEventEdtDlg");
    }

    private void onEventEditClick(final SignificantEvent event) {
        FragmentManager manager = getFragmentManager();
        SignificantEventActionDialog dialog = new SignificantEventActionDialog();

        dialog.setListener(new SignificantEventActionDialog.OnSignificantEventActionClick() {
            @Override
            public void onEditClick() {
                onSignificantEventEdit(event);
            }

            @Override
            public void onDeleteClick() {
                onDeleteSignificantEvent(event);
            }
        });
        dialog.show(manager, "sgnEventActDlg");
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
        finish();
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

    @Override
    public void onDataSetChanged(int eventId, String eventName, Date eventDate, long reminderTime) {
        if (eventId == INVALID_EVENT_ID) {
            onCreateNewEvent(eventName, eventDate, reminderTime);
        } else {
            onChangeEventDate(eventId, eventName, eventDate, reminderTime);
        }
    }

    private void onChangeEventDate(int eventId, String eventName, Date eventDate, long reminderTime) {
        SignificantEvent event = eventsAdapter.getItemById(eventId);
        if (event == null) return;

        event.setTitle(eventName);
        event.setDate(eventDate);
        event.setReminderTime(reminderTime);

        eventsAdapter.notifyDataSetChanged();
        // todo save changed significant event to db
    }

    private void onCreateNewEvent(String eventName, Date eventDate, long reminderTime) {
        SignificantEvent event = new SignificantEvent(eventName, eventDate, reminderTime);
        // todo save new event into db
        eventsAdapter.add(event);
        eventsAdapter.notifyDataSetChanged();
    }

    @Data
    @NoArgsConstructor
    public static class SignificantEvent {

        private static final int CUSTOM_EVENT_ICON = R.drawable.ic_event_special;

        private int id;
        private String title;
        //todo add event icon
        private Date date;
        private boolean enabled;
        private boolean modified;
        private long reminderTime;
        private int iconResId;

        public SignificantEvent(String title, Date date, long reminderTime) {
            this.title = title;
            this.date = date;
            this.modified = true;
            this.reminderTime = reminderTime;
        }

        public int getIconResId() {
            return (iconResId <= 0) ? CUSTOM_EVENT_ICON : iconResId;
        }
    }


    @Data
    @NoArgsConstructor
    private static class SignificantEventHolder {
        View enableEventButton;
        TextView title;
        CheckBox checkBox;
        CircleImageView icon;

        public void setEventValues(SignificantEvent event) {
            title.setText(event.getTitle());
            checkBox.setChecked(event.isEnabled());
            icon.setImageResource(event.getIconResId());
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
            processEnableEventButtonClick(viewHolder, position);
            return view;
        }

        @Override
        public void add(SignificantEvent event) {
            if (event != null) {
                values.add(event);
            }
        }

        private void processEnableEventButtonClick(SignificantEventHolder viewHolder, final int eventPosition) {
            viewHolder.getEnableEventButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SignificantEvent event = getItem(eventPosition);
                    onSignificantEventCheckBoxClick(event);
                }
            });
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
            holder.setEnableEventButton(view.findViewById(R.id.enable_significant_event_button));
            view.setTag(holder);
            return holder;
        }

        @Override
        public SignificantEvent getItem(int position) {
            return values.get(position);
        }

        @Override
        public int getCount() {
            return values.size();
        }

        public void deleteEvent(SignificantEvent event) {
            boolean isChanged = false;
            Iterator<SignificantEvent> it = values.iterator();
            while (it.hasNext()) {
                SignificantEvent next = it.next();
                if (next.getId() == event.getId()) {
                    it.remove();
                    isChanged = true;
                }
            }

            if (isChanged) {
                notifyDataSetChanged();
            }
        }

        public SignificantEvent getItemById(int eventId) {
            for (SignificantEvent event: values) {
                if (event.getId() == eventId) {
                    return event;
                }
            }
            return null;
        }
    }

}
