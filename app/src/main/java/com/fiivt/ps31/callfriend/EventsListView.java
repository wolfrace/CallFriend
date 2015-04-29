package com.fiivt.ps31.callfriend;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fiivt.ps31.callfriend.AppDatabase2.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;
import lombok.Getter;

public class EventsListView extends LinearLayout {

    private final static Comparator<Event> DAYS_LEFT_COMPARATOR = new Comparator<Event>() {
        @Override
        public int compare(Event e1, Event e2) {
            Date d1 = e1.getDate();
            Date d2 = e2.getDate();
            return (d1 == null) ? -1 : d1.compareTo(d2);
        }
    };

    public boolean isEmpty() {
        return eventAdapter.isEmpty();
    }

    public interface OnEventClickListener {
        public void onClick(Event event);
    }

    private int daysLeftColor;
    private EventAdapter eventAdapter;
    private OnEventClickListener clickListener;

    public EventsListView(Context context) {
        this(context, null);
    }

    public EventsListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.events_list_layout, this);

        processAttributes(context, attrs);
        initEventsList();
    }

    public void removeById(int id) {
        boolean isRemoved = false;
        Iterator<Event> it = eventAdapter.getValues().iterator();
        while (it.hasNext()) {
            Event event = it.next();
            if (event.getId() == id) {
                it.remove();
                isRemoved = true;
            }
        }

        if (isRemoved) {
            notifyOnChanged();
        }
    }

    public void setClickListener(OnEventClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // HACK! TAKE THAT ANDROID!
        if (isExpanded())
        {
            // Calculate entire height by providing a very large height hint.
            // But do not use the highest 2 bits of this integer; those are
            // reserved for the MeasureSpec mode.
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void addAll(Collection<Event> events) {
        if (events != null && !events.isEmpty()) {
            eventAdapter.getValues().addAll(events);
            notifyOnChanged();
        }
    }

    public void add(Event event) {
        if (event != null) {
            eventAdapter.getValues().add(event);
            notifyOnChanged();
        }
    }

    private void initEventsList() {
        final ListView listView = (ListView) findViewById(R.id.events_list_view);
        eventAdapter = new EventAdapter(getContext());
        listView.setAdapter(eventAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if (clickListener != null) {
                    Event event = ((EventAdapter) adapterView.getAdapter()).getValues().get(pos);
                    clickListener.onClick(event);
                }
            }
        });
        notifyOnChanged();
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EventsListView, 0, 0);
        String titleText = a.getString(R.styleable.EventsListView_titleText);
        daysLeftColor = a.getColor(R.styleable.EventsListView_dayLeftColor, R.color.text_days_left_color_urgently);
        a.recycle();

        TextView titleView = (TextView) findViewById(R.id.event_list_title_text);
        titleView.setText(titleText);
    }

    public boolean isExpanded() {
        return true;
    }

    @Data
    private static class EventViewHolder {
        private TextView name;
        private TextView title;
        private ImageView personImage;
        private ImageView eventImage;
        private TextView daysLeft;

        public void setEventValues(Event event) {
            name.setText(event.getPerson().getName());
            title.setText(event.getInfo());
            daysLeft.setText(Integer.toString(event.getDaysLeft()));
            //personImage.setImageResource();
            //eventImage.setImageResource();
        }
    }

    private void notifyOnChanged() {
        int visibility = eventAdapter.getValues().isEmpty()
            ? View.GONE
            : View.VISIBLE;
        setVisibility(visibility);
        eventAdapter.notifyDataSetChanged();
    }

    public class EventAdapter extends ArrayAdapter<Event> {
        @Getter
        private final List<Event> values;
        private final Context context;

        public EventAdapter(Context context) {
            super(context, R.layout.event_list_item);
            this.context = context;
            this.values = new ArrayList<Event>();
        }

        @Override
        public void notifyDataSetChanged() {
            Collections.sort(values, DAYS_LEFT_COMPARATOR);
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getViewWithHolder(convertView, parent);
            EventViewHolder viewHolder = (EventViewHolder) view.getTag();
            Event event = values.get(position);
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
            return inflater.inflate(R.layout.event_list_item, parent, false);
        }

        private EventViewHolder initializeHolder(View view) {
            EventViewHolder holder = new EventViewHolder();
            holder.setName((TextView) view.findViewById(R.id.event_list_contact_name));
            holder.setTitle((TextView) view.findViewById(R.id.event_list_event_title));
            holder.setDaysLeft((TextView) view.findViewById(R.id.events_days_left));
            holder.setEventImage((CircleImageView) view.findViewById(R.id.event_image));
            holder.setPersonImage((CircleImageView) view.findViewById(R.id.profile_image));
            view.setTag(holder);

            setDaysLeftColor(view, daysLeftColor);
            return holder;
        }

        private void setDaysLeftColor(View view, int daysLeftColor) {
            TextView daysLeftSuffix = (TextView) view.findViewById(R.id.events_days_left_suffix);
            TextView daysLeftView = (TextView) view.findViewById(R.id.events_days_left);

            daysLeftSuffix.setTextColor(daysLeftColor);
            daysLeftView.setTextColor(daysLeftColor);
        }

        @Override
        public int getCount() {
            return values.size();
        }

    }

}
