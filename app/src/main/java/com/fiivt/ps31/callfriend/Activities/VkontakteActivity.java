package com.fiivt.ps31.callfriend.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.BaseActivity;
import com.fiivt.ps31.callfriend.R;
import com.fiivt.ps31.callfriend.Utils.EventsGenerator;
import com.fiivt.ps31.callfriend.Utils.Settings;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

// ��������� ����������� ��� Android:
// A3DD8FF0F176C44FDA248C7156DA8366380CA2BC
// BB58C88B1C43570A79DC1043794830D377702F12

public class VkontakteActivity extends BaseActivity {

    private final static String VK_APP_ID = "4907734";

    //private static String VK_ACCESS_TOKEN = "eLEwiwRgTdVsc160bLgW";
    private static String VK_ACCESS_TOKEN = "eLEwiwRgTdVsc160bLgW";
    private static String[] VK_SCOPE = new String[]{VKScope.FRIENDS, VKScope.NOHTTPS};
    private static final long DEFAULT_REMINDER_TIME = TimeUnit.DAYS.toMillis(1);

    private VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(VK_SCOPE);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            new AlertDialog.Builder(VkontakteActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(VkontakteActivity.this, VK_ACCESS_TOKEN);
//            Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
//            startActivity(i);
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
//            Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
//            startActivity(i);
        }
    };

    Context getContext() {
        return VKUIHelper.getTopActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Settings settings = Settings.getInstance(this);
        settings.setIsImportVkNeed(true);
        if (settings.isImportVkNeed() == false) {
            Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
            startActivity(i);
            finish();
            return;
        }

        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, VK_APP_ID, VKAccessToken.tokenFromSharedPreferences(this, VK_ACCESS_TOKEN));
        VKSdk.authorize(VK_SCOPE);

        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "sex,bdate,city"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                parseJsonResponse(response);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.import_compete), Toast.LENGTH_SHORT).show();

                //Do complete stuff
                Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
                startActivity(i);
                //finish();

            }

            @Override
            public void onError(VKError error) {
                //Do error stuff
                Toast toast = Toast.makeText(getApplicationContext(),
                        error.errorMessage, Toast.LENGTH_SHORT);
                toast.show();
                //Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
                //startActivity(i);
                //finish();
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                //I don't really believe in progress
                //Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
                //startActivity(i);
                //I don't really believe in progress
            }
        });
    }

    protected void parseJsonResponse(VKResponse response) {
        if (response != null) {
            try {
                AppDb appDb = new AppDb(this);
                // TO DELETE
                appDb.clearDb(this);

                JSONObject json = response.json;
                JSONObject ara = json.getJSONObject("response");
                JSONArray friends = ara.getJSONArray("items");
                for (int i = 0; i < friends.length(); ++i) {
                    JSONObject f = friends.getJSONObject(i);
                    String firstName = f.getString("first_name");
                    String lastName = f.getString("last_name");
                    Boolean isMale = (f.getInt("sex") == 2);

                    String description;
                    try {
                        JSONObject city = f.getJSONObject("city");
                        description = city.getString("title");
                    }
                    catch (Exception e) {
                        description = "Из ВКонтакте";
                    }

                    //String dateStr = f.getString("bdate");
                    //SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy");
//                    try {
//                        Date birthDate = sdf.parse(dateStr);
//                        // TODO add photo
//                        Person p = new Person(firstName.concat(" ").concat(lastName), isMale, 0);
//                        appDb.addPerson(p);
//                    }
//                    catch (ParseException pe) {
//                    }
                    Person p = new Person(firstName.concat(" ").concat(lastName), description, isMale, "");
                    appDb.addPerson(p);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

            new Thread(new Runnable() {
                Context context;
                @Override
                public void run() {
                    EventsGenerator.generate(context);
                }
                public Runnable init(Context context){
                    this.context = context;
                    return this;
                }
            }.init(this)).start();
        }
        else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        //....
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
        //....
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
        //....
    }
}
