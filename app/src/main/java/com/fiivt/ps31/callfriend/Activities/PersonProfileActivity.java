package com.fiivt.ps31.callfriend.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import at.markushi.ui.CircleButton;
import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.R;
import lombok.Data;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new AppDb(this);

        initView();

        RelativeLayout addPersonButton = (RelativeLayout)findViewById(R.id.edit_person);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonProfileActivity.this, FriendEdit.class);
                intent.putExtra("person", person);
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            person = (Person) bundle.getSerializable("person");
        }

        setAvatar();
        setPersonName();
        setPersonNote();
    }

    private void setAvatar() {
//        for VK
//        URL url = new URL("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464");
//        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//        imageView.setImageBitmap(bmp);

        avatarImagePath = person.getIdPhoto();
        if (avatarImagePath != "") {
            //Toast.makeText(getApplicationContext(), "IN", Toast.LENGTH_SHORT).show();
            avatarView.setImageURI(Uri.parse(avatarImagePath));
            avatarView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void setPersonName() {
        personName.setText(person.getName());
    }

    private void setPersonNote() {
        personNote.setText(person.getDescription());
    }

    private void initView() {
        setContentView(R.layout.person_profile_view);
        avatarView = (ImageView) findViewById(R.id.person_profile_photo);
        personName = (TextView) findViewById(R.id.person_profile_name);
        personNote = (TextView) findViewById(R.id.person_profile_note);

    }
}
