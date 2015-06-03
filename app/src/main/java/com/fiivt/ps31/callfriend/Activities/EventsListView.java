package com.fiivt.ps31.callfriend.Activities;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fiivt.ps31.callfriend.AppDatabase.Event;
import com.fiivt.ps31.callfriend.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;
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

    public Bitmap mPersonImagePlaceholder;
    private LruCache<String, Bitmap> mMemoryCache;

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

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        mPersonImagePlaceholder = BitmapFactory.decodeResource(getResources(), R.drawable.friend_avatar);

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
    private class EventViewHolder {
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
            eventImage.setImageResource(event.getPersonTemplate().getIconResId());
            setAvatar(event.getPerson().getIdPhoto());
        }

        private void setAvatar(final String path) {

            if (!path.equals("")) {
                loadBitmap(path, personImage);
            }
        }

        public void loadBitmap(String uriString, ImageView imageView) {
            final String imageKey = uriString;

            final Bitmap bitmap = getBitmapFromMemCache(imageKey);
            if (bitmap != null) {
                personImage.setImageBitmap(bitmap);
            } else {
                if (cancelPotentialWork(uriString, imageView)) {
                    final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                    final AsyncDrawable asyncDrawable =
                            new AsyncDrawable(getResources(), mPersonImagePlaceholder, task);
                    imageView.setImageDrawable(asyncDrawable);
                    task.execute(uriString);
                }
            }
        }

    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;
        private String data = "";

        public BitmapWorkerTask(ImageView imageView) {
            //to ensure the ImageView can be garbageCollected
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        //Decode image in background
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            ImageView thumbnail = imageViewWeakReference.get();
            Bitmap bitmap = decodeSampledBitmapFromStream(data, 40, 40);
            addBitmapToMemoryCache(data, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewWeakReference != null && bitmap != null) {
                final ImageView imageView = imageViewWeakReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }


    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if ( !bitmapData.equals("") || bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public Bitmap decodeSampledBitmapFromStream(String fileUriString, int reqWidth, int reqHeight) {
        Bitmap resBmp = null;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            InputStream is = getContext().getContentResolver().openInputStream(Uri.parse(fileUriString));
            BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        try {
            InputStream is = getContext().getContentResolver().openInputStream(Uri.parse(fileUriString));
            resBmp = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resBmp;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //take raw parameters of img
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 8;

        if (reqHeight == 0 || reqWidth == 0) return inSampleSize;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
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
