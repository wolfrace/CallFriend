package com.fiivt.ps31.callfriend.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
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

import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.EventTemplate;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate;
import com.fiivt.ps31.callfriend.SignificantEventActionDialog;
import com.fiivt.ps31.callfriend.SignificantEventEditDialog;
import com.fiivt.ps31.callfriend.SignificantEventEditDialog.OnDataSetChangedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.ExpandedListView;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class FriendEdit extends Activity implements OnDataSetChangedListener {

    private static final int INVALID_EVENT_ID = -1;
    private static final long DEFAULT_REMINDER_TIME = TimeUnit.DAYS.toMillis(1);

    private AppDb db;

    private Person person;
    private EditText nameView;
    private EditText descriptionView;
    private CircleImageView avatarView;
    private Integer hiddenPersonId;
    private SignificantEventAdapter eventsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //test
        //startService(new Intent(this, EventService.class));
        super.onCreate(savedInstanceState);

        db = AppDb.getInstance(this);
        initView();

        //todo init person and events
        //setPersonDataOnView(person, events);
    }

    private List<PersonTemplate> convertToSignificantEvents(List<com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate> templates) {
        List<PersonTemplate> events = new ArrayList<PersonTemplate>(templates.size());
        for (com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate template: templates) {
            new PersonTemplate();
        }
        return events;
    }

    private List<com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate> generateListOfPersonTemplates(Person person) {
        List<EventTemplate> defaultTemplates = db.getEventTemplates();
        List<com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate> personTemplates = new ArrayList<com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate>(defaultTemplates.size());
        for (EventTemplate defTemplate: defaultTemplates) {
            com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate personTemplate = generatePersonTemplate(person, defTemplate);
            personTemplates.add(personTemplate);
        }
        return personTemplates;
    }

    private com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate generatePersonTemplate(Person person, EventTemplate defaultTemplate) {
        return new com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate(
                person,
                defaultTemplate,
                defaultTemplate.getDefaultDate(),
                new Date(TimeUnit.DAYS.toMillis(365)),
                DEFAULT_REMINDER_TIME,
                false);
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

    private void setPersonDataOnView(Person person, List<PersonTemplate> events) {
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
                PersonTemplate event = eventsAdapter.getItem(position);
                onEventEditClick(event);
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

    private void onDeleteSignificantEvent(PersonTemplate event) {
        //todo delete from db ??
        eventsAdapter.deleteEvent(event);
    }

    private void onSignificantEventEdit(PersonTemplate event) {
        showSignificantEventEditDialog(event);
    }

    private void onSignificantEventCheckBoxClick(PersonTemplate event) {
        boolean isInitialized = event.getCustomDate() != null;
        if (isInitialized) {
            event.setEnabled(!event.isEnabled());
            eventsAdapter.notifyDataSetChanged();
        } else {
            event.setEnabled(true);
            showSignificantEventEditDialog(event);
        }
    }

    private void showSignificantEventEditDialog(PersonTemplate event) {
        FragmentManager manager = getFragmentManager();
        SignificantEventEditDialog dialog = new SignificantEventEditDialog();

        Bundle args = new Bundle();
        if (event != null){
            args.putInt("id", event.getId());
            args.putInt("iconResId", event.getIconResId());
            args.putString("eventName", event.getTitle());
            args.putSerializable("eventDate", event.getCustomDate());
            args.putLong("reminderTime", event.getReminderTime());
        } else {
            args.putInt("id", INVALID_EVENT_ID);
        }

        dialog.setArguments(args);
        dialog.setListener(this);
        dialog.show(manager, "sgnEventEdtDlg");
    }

    private void onEventEditClick(final PersonTemplate event) {
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
        PersonTemplate event = eventsAdapter.getItemById(eventId);
        if (event == null) return;

        event.setTitle(eventName);
        event.setCustomDate(eventDate);
        event.setReminderTime(reminderTime);

        eventsAdapter.notifyDataSetChanged();
        // todo save changed significant event to db
    }

    private void onCreateNewEvent(String eventName, Date eventDate, long reminderTime) {
        PersonTemplate event = new PersonTemplate(person, eventName, eventDate, reminderTime);
        db.addPersonTemplate(event);
        eventsAdapter.add(event);
        eventsAdapter.notifyDataSetChanged();
    }

    @Data
    @NoArgsConstructor
    private static class SignificantEventHolder {
        View enableEventButton;
        TextView title;
        CheckBox checkBox;
        CircleImageView icon;

        public void setEventValues(PersonTemplate event) {
            title.setText(event.getTitle());
            checkBox.setChecked(event.isEnabled());
            icon.setImageResource(event.getIconResId());
        }
    }

    public class SignificantEventAdapter extends ArrayAdapter<PersonTemplate> {
        @Getter
        private final List<PersonTemplate> values;
        private final Context context;

        public SignificantEventAdapter(Context context) {
            super(context, R.layout.row_significant_event);
            this.context = context;
            this.values = new ArrayList<PersonTemplate>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getViewWithHolder(convertView, parent);
            SignificantEventHolder viewHolder = (SignificantEventHolder) view.getTag();
            PersonTemplate event = values.get(position);
            viewHolder.setEventValues(event);
            processEnableEventButtonClick(viewHolder, position);
            return view;
        }

        @Override
        public void add(PersonTemplate event) {
            if (event != null) {
                values.add(event);
            }
        }

        private void processEnableEventButtonClick(SignificantEventHolder viewHolder, final int eventPosition) {
            viewHolder.getEnableEventButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PersonTemplate event = getItem(eventPosition);
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
        public PersonTemplate getItem(int position) {
            return values.get(position);
        }

        @Override
        public int getCount() {
            return values.size();
        }

        public void deleteEvent(PersonTemplate event) {
            boolean isChanged = false;
            Iterator<PersonTemplate> it = values.iterator();
            while (it.hasNext()) {
                PersonTemplate next = it.next();
                if (next.getId().equals(event.getId())) {
                    it.remove();
                    isChanged = true;
                }
            }

            if (isChanged) {
                notifyDataSetChanged();
            }
        }

        public PersonTemplate getItemById(int eventId) {
            for (PersonTemplate event: values) {
                if (event.getId() == eventId) {
                    return event;
                }
            }
            return null;
        }
    }

}
