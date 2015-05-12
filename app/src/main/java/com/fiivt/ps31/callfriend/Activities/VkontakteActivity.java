package com.fiivt.ps31.callfriend.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.fiivt.ps31.callfriend.AppDatabase.AppDb;
import com.fiivt.ps31.callfriend.AppDatabase.Person;
import com.fiivt.ps31.callfriend.Utils.Settings;
import com.vk.sdk.*;
import com.vk.sdk.api.*;
import com.vk.sdk.api.methods.VKApiFriends;
import com.vk.sdk.dialogs.VKCaptchaDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

// ��������� ����������� ��� Android:
// A3DD8FF0F176C44FDA248C7156DA8366380CA2BC
// BB58C88B1C43570A79DC1043794830D377702F12

public class VkontakteActivity extends Activity {

    private final static String VK_APP_ID = "4907734";

    private static String VK_ACCESS_TOKEN = "eLEwiwRgTdVsc160bLgW";
    private static String[] VK_SCOPE = new String[]{VKScope.FRIENDS, VKScope.NOHTTPS};

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
            Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
            //startActivity(i);
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
            startActivity(i);
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
                //Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
                //startActivity(i);
                //Do complete stuff
            }

            @Override
            public void onError(VKError error) {
                Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
                startActivity(i);
                //Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Intent i = new Intent(VkontakteActivity.this, PersonActivity.class);
                startActivity(i);
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
                    //String city = f.getString("city");
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
                    Person p = new Person(firstName.concat(" ").concat(lastName), "imported from vk",isMale, 0);
                    appDb.addPerson(p);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
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
