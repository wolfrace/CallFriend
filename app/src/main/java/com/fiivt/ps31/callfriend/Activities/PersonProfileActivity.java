package com.fiivt.ps31.callfriend.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import at.markushi.ui.CircleButton;
import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.FriendLastActive;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Данил on 28.05.2015.
 */

public class PersonProfileActivity extends Activity {

    public AppDb database;
    private Person person;
    private String avatarImagePath;
    private ImageView avatarView;
    private TextView personName;
    private TextView personNote;
    private ArrayAdapter eventPersonProfileAdapter;
    private RelativeLayout tab1;
    private RelativeLayout tab2;
    private LinearLayout tabContent;
    private LinearLayout eventNotExciting;
    private int eventCount;
    private int eventHistoryCount;
    private ListView eventsListView;
    private ListView eventsHistoryListView;
    private LinearLayout eventHistoryNotExciting;
    private int ZERO_YEAR = -1899;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new AppDb(this);

        initView();

        RelativeLayout addPersonButton = (RelativeLayout)findViewById(R.id.edit_person);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonProfileActivity.this, ImageChooserActivity.class);
                intent.putExtra("person", person);
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            person = (Person) bundle.getSerializable("person");
        }

        ListView EventPersonProfileListView = (ListView) findViewById(R.id.person_profile_event_list);
        List<Event> event = database.getEventsByPerson(person.getId());
        eventCount = event.size();
        eventHistoryCount = 0;
        ResizeTabContent(eventCount);
        eventPersonProfileAdapter = new EventPersonProfileArrayAdapter(this, event);
        EventPersonProfileListView.setAdapter(eventPersonProfileAdapter);

        ListView EventPersonHistoryProfileListView = (ListView) findViewById(R.id.person_profile_event_history_list);
        List<Event> eventHistory = database.getLastEventsByPerson(person.getId());;
        eventHistoryCount = eventHistory.size();
        ResizeTabContent(eventCount);
        eventPersonProfileAdapter = new EventPersonProfileArrayAdapter(this, eventHistory);

        EventPersonHistoryProfileListView.setAdapter(eventPersonProfileAdapter);


        setAvatar();
        setPersonName();
        setPersonNote();

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        // инициализация
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec("tag1");
        // название вкладки
        tabSpec.setIndicator("События");
        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(R.id.tvTab2);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        // указываем название и картинку
        // в нашем случае вместо картинки идет xml-файл,
        // который определяет картинку по состоянию вкладки
        tabSpec.setIndicator("История", getResources().getDrawable(R.drawable.tab_icon_selector));
        tabSpec.setContent(R.id.tvTab1);
        tabHost.addTab(tabSpec);

        // вторая вкладка будет выбрана по умолчанию
        tabHost.setCurrentTabByTag("tag1");
        tab1.setVisibility(View.VISIBLE);
        tab2.setVisibility(View.GONE);
        if(eventCount == 0)
        {
            ResizeTabContent(3);
            eventNotExciting.setVisibility(View.VISIBLE);
            tab1.setVisibility(View.VISIBLE);
            eventsListView.setVisibility(View.GONE);
        }




        // обработчик переключения вкладок
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                if(tab1.getVisibility() == View.VISIBLE ){

                    tab1.setVisibility(View.GONE);
                    tab2.setVisibility(View.VISIBLE);
                    ResizeTabContent(eventHistoryCount);
                    if(eventHistoryCount == 0)
                    {
                        ResizeTabContent(3);
                        eventHistoryNotExciting.setVisibility(View.VISIBLE);
                        eventsHistoryListView.setVisibility(View.GONE);
                    }
                    else{
                        ResizeTabContent(eventHistoryCount);
                        eventHistoryNotExciting.setVisibility(View.GONE);
                        eventsHistoryListView.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    tab1.setVisibility(View.VISIBLE);
                    tab2.setVisibility(View.GONE);

                    if(eventCount == 0)
                    {
                        ResizeTabContent(3);
                        eventNotExciting.setVisibility(View.VISIBLE);
                        eventsListView.setVisibility(View.GONE);
                    }
                    else{
                        ResizeTabContent(eventCount);
                        eventNotExciting.setVisibility(View.GONE);
                        eventsListView.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }

    private void setAvatar() {
//        for VK
//        URL url = new URL("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464");
//        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//        imageView.setImageBitmap(bmp);

        avatarImagePath = person.getIdPhoto();
        avatarView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (avatarImagePath != "") {
            //Toast.makeText(getApplicationContext(), "IN", Toast.LENGTH_SHORT).show();
            avatarView.setImageURI(Uri.parse(avatarImagePath));

        }
    }

    public String getCustomDateString(Date customDate){
        String pattern = "d MMMM yyyy 'г.'";
        if (ZERO_YEAR == customDate.getYear())
            pattern = "d MMMM";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("ru", "RU"));
        return sdf.format(customDate);

    }
    private void setPersonName() {
        String name = person.getName();
        if (name.length() <= 14){
            personName.setText(name);
        }
        else{
            personName.setText(name.substring(0, 14) + "...");
        }

    }

    private void setPersonNote() {
        personNote.setText(person.getDescription());
    }

    private void initView() {
        setContentView(R.layout.person_profile_view);
        avatarView = (ImageView) findViewById(R.id.person_profile_photo);
        personName = (TextView) findViewById(R.id.person_profile_name);
        personNote = (TextView) findViewById(R.id.person_profile_note);
        tab1 = (RelativeLayout) findViewById(R.id.tvTab1);
        tab2 = (RelativeLayout) findViewById(R.id.tvTab2);
        eventsHistoryListView = (ListView) findViewById(R.id.person_profile_event_history_list);
        eventHistoryNotExciting = (LinearLayout) findViewById(R.id.events_history_profile_not_existing_notify);
        tabContent = (LinearLayout) findViewById(R.id.tabContent);
        eventNotExciting = (LinearLayout) findViewById(R.id.events_profile_not_existing_notify);
        eventsListView = (ListView) findViewById(R.id.person_profile_event_list);
        eventHistoryCount = 0;
    }

    private void ResizeTabContent(int count){
            tabContent.setMinimumHeight(count * 60 + 50);
    }

    @Data
    class EventPersonProfileViewHolder {
        private TextView name;
        private CircleImageView image;
        private TextView date;

        public void setEventPersonProfileValues(Event event) {

            name.setText(event.getInfo());
            date.setText(getCustomDateString(event.getDate()));
            image.setImageResource(event.getPersonTemplate().getIconResId());
        }

    }

    public class EventPersonProfileArrayAdapter extends ArrayAdapter<Event> {
        private final Context context;
        private final List<Event> values;

        public EventPersonProfileArrayAdapter(Context context, List<Event> values) {
            super(context, R.layout.person_profile_event_item, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getViewWithHolder(convertView, parent);
            EventPersonProfileViewHolder viewHolder = (EventPersonProfileViewHolder) view.getTag();
            Event event = values.get(position);
            viewHolder.setEventPersonProfileValues(event);
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
            return inflater.inflate(R.layout.person_profile_event_item, parent, false);
        }

        private EventPersonProfileViewHolder initializeHolder(View view) {

            EventPersonProfileViewHolder holder = new EventPersonProfileViewHolder();
            holder.setName((TextView) view.findViewById(R.id.person_profile_event_title));
            holder.setImage((CircleImageView) view.findViewById(R.id.person_profile_image));
            holder.setDate((TextView) view.findViewById(R.id.person_profile_event_date));
            view.setTag(holder);
            return holder;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Event getItem(int pos) {
            return values.get(pos);
        }
    }

}
