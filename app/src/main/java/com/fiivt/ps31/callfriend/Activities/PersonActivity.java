package com.fiivt.ps31.callfriend.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.LruCache;
import android.view.*;
import android.widget.*;

import at.markushi.ui.CircleButton;
import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.BaseActivity;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.FriendLastActive;

import java.io.InputStream;
import java.lang.ref.WeakReference;
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

    public Bitmap mPersonImagePlaceholder;
    private LruCache<String, Bitmap> mMemoryCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        database = new AppDb(this);
        mPersonImagePlaceholder = BitmapFactory.decodeResource(getResources(), R.drawable.friend_avatar);
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

        addFriendsButton.setOnClickListener(new View.OnClickListener() {
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
        private Thread imageLoaderThread;
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


            String pathUriString = person.getIdPhoto();
            setAvatar(pathUriString);
        }

        private void setAvatar(final String path) {

            if (!path.equals("")) {
                loadBitmap(path, image);
            }
        }

        public void loadBitmap(String uriString, ImageView imageView) {
            final String imageKey = uriString;

            final Bitmap bitmap = getBitmapFromMemCache(imageKey);
            if (bitmap != null) {
                image.setImageBitmap(bitmap);
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
            InputStream is = getContentResolver().openInputStream(Uri.parse(fileUriString));
            BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        try {
            InputStream is = getContentResolver().openInputStream(Uri.parse(fileUriString));
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
