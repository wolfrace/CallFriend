package com.fiivt.ps31.callfriend.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.vk.sdk.*;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;

// ��������� ����������� ��� Android:
// A3DD8FF0F176C44FDA248C7156DA8366380CA2BC
// BB58C88B1C43570A79DC1043794830D377702F12

public class VkontakteActivity extends Activity {

    private final static String VK_APP_ID = "4907734";

    //private static String VK_ACCESS_TOKEN = "eLEwiwRgTdVsc160bLgW";
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
            Intent i = new Intent(VkontakteActivity.this, FriendEdit.class);
            startActivity(i);
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Intent i = new Intent(VkontakteActivity.this, FriendEdit.class);
            startActivity(i);
        }
    };

    Context getContext() {
        return VKUIHelper.getTopActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, VK_APP_ID, VKAccessToken.tokenFromSharedPreferences(this, VK_ACCESS_TOKEN));
        //setContentView(R.layout.vk_share_dialog);
        //VKSdk.authorize(VK_SCOPE, true, true);
        VKSdk.authorize(VK_SCOPE);
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
