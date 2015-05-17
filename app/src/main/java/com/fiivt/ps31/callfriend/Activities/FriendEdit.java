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
import android.widget.Toast;

import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.EventTemplate;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.AppDatabase.PersonTemplate;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.SignificantEventActionDialog;
import com.fiivt.ps31.callfriend.SignificantEventEditDialog;
import com.fiivt.ps31.callfriend.SignificantEventEditDialog.OnDataSetChangedListener;
import com.fiivt.ps31.callfriend.SignificantEventEditDialog.OnSuccessListener;
import com.fiivt.ps31.callfriend.Utils.ExpandedListView;
import com.fiivt.ps31.callfriend.Utils.IdGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class FriendEdit extends Activity implements OnDataSetChangedListener {

    private static final int INVALID_EVENT_ID = Integer.MAX_VALUE;
    private static final long DEFAULT_REMINDER_TIME = TimeUnit.DAYS.toMillis(1);
    private static final Date INVALID_DATE = new Date(0);

    private AppDb db;

    private Person person;
    private boolean isNewUser;
    private List<Integer> removedTemplateIds;
    private List<PersonTemplate> personTemplates;

    private EditText nameView;
    private EditText descriptionView;
    private CircleImageView avatarView;
    private SignificantEventAdapter eventsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDb.getInstance(this);

        initData();
        initView();
        setPersonDataOnView(person, personTemplates);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            person = (Person) bundle.getSerializable("person");
        }

        isNewUser = false;
        if (person == null) {
            person = new Person();
            isNewUser = true;
        }

        removedTemplateIds = new ArrayList<Integer>(0);
        personTemplates = getPersonalTemplates(person);
    }

    private List<PersonTemplate> getPersonalTemplates(Person person){
        boolean isNewUser = person.getId() <= 0 || db.getPersonTemplates(person).size() == 0;
        return isNewUser
                ? generateNewPersonTemplates(person)
                : db.getPersonTemplates(person);
    }


    private List<PersonTemplate> generateNewPersonTemplates(Person person) {
        List<EventTemplate> defaultTemplates = db.getEventTemplates();
        List<PersonTemplate> personTemplates = new ArrayList<PersonTemplate>(defaultTemplates.size());
        for (EventTemplate defTemplate: defaultTemplates) {
            PersonTemplate personTemplate = generatePersonTemplate(person, defTemplate);
            personTemplates.add(personTemplate);
        }
        return personTemplates;
    }

    private PersonTemplate generatePersonTemplate(Person person, EventTemplate defaultTemplate) {
        return new PersonTemplate(
                IdGenerator.generate(),
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
        descriptionView.setText(person.getDescription());
        //avatarView.setImageResource(); todo set AVATAR

        // set significant events
        eventsAdapter.setValues(events);
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
        showSignificantEventEditDialog(null, null);
    }

    private void onDeleteSignificantEvent(PersonTemplate event) {
        if (event.getId() >= 0){
            removedTemplateIds.add(event.getId());
        }
        eventsAdapter.deleteEvent(event);
    }

    private void onSignificantEventEdit(PersonTemplate event) {
        showSignificantEventEditDialog(event, null);
    }

    private void onSignificantEventCheckBoxClick(final PersonTemplate event) {
        Date date = event.getCustomDate();
        boolean isInitialized = date != null && !INVALID_DATE.equals(date);
        if (isInitialized) {
            event.setEnabled(!event.isEnabled());
            eventsAdapter.notifyDataSetChanged();
        } else {
            OnSuccessListener onSuccess = new OnSuccessListener() {
                @Override
                public void onSuccess(int eventId) {
                    event.setEnabled(true);
                    eventsAdapter.notifyDataSetChanged();
                }
            };
            showSignificantEventEditDialog(event, onSuccess);
        }
    }

    private void showSignificantEventEditDialog(PersonTemplate event, OnSuccessListener onSuccess) {
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
        dialog.setOnSuccessListener(onSuccess);
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
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View cutomView = mInflater.inflate(R.layout.friend_edit_action_bar, null);
        setMinimalWidthAsScreenWidth(cutomView);
        actionBar.setCustomView(cutomView);
        actionBar.setDisplayShowCustomEnabled(true);

        setActionBarTitle(cutomView);
        ImageButton saveButton = (ImageButton) cutomView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onSave();
            }
        });

        ImageButton cancelButton = (ImageButton) cutomView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onCancel();
            }
        });
    }

    private void setActionBarTitle(View actionBar) {
        int titleResId = isNewUser
                ? R.string.add_contact_layout_title
                : R.string.change_contact_layout_title;

        String title = getString(titleResId);
        TextView titleView = (TextView) actionBar.findViewById(R.id.friend_action_bar_title);
        titleView.setText(title);
    }

    public void onSave() {
        Person person = getPersonDataFromView();

        if (person == null)
            return;

        if (person.getId() <= 0) {
            db.addPerson(person);
        } else {
            db.updatePerson(person);
        }

        for (Integer id: removedTemplateIds){
            db.deletePersonTemplate(id);
        }

        for (PersonTemplate pt: personTemplates) {
            if (pt.getId() <= 0) {
                db.addPersonTemplate(pt);
            } else {
                db.updatePersonTemplate(pt);
            }
        }

        close();
    }

    public void onCancel() {
        finish();
    }

    public void close(){
        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra("selectedPos", 0);
        startActivity(intent);
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

        String name = nameView.getText().toString();
        String description = descriptionView.getText().toString();

        if (name.isEmpty()) {
            onError(R.string.empty_person_name_hint);
            return null;
        }

        person.setName(name);
        person.setDescription(description);
        return person;
    }

    private void onError(int errorStringResId) {
        Toast
                .makeText(this, errorStringResId, Toast.LENGTH_LONG)
                .show();
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
    }

    private void onCreateNewEvent(String eventName, Date eventDate, long reminderTime) {
        PersonTemplate event = new PersonTemplate(IdGenerator.generate(), person, eventName, eventDate, reminderTime);
        eventsAdapter.add(event);
        eventsAdapter.notifyDataSetChanged();
    }

    @Data
    @NoArgsConstructor
    private static class SignificantEventHolder {
        View checkBoxButton;
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
        private List<PersonTemplate> values;
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
            processCheckBoxButtonClick(viewHolder, position);
            return view;
        }

        @Override
        public void add(PersonTemplate event) {
            if (event != null) {
                values.add(event);
            }
        }

        private void processCheckBoxButtonClick(SignificantEventHolder viewHolder, final int eventPosition) {
            viewHolder.getCheckBoxButton().setOnClickListener(new View.OnClickListener() {
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
            holder.setCheckBoxButton(view.findViewById(R.id.enable_significant_event_button));
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

        public void setValues(List<PersonTemplate> values) {
            this.values = values;
        }
    }

}
