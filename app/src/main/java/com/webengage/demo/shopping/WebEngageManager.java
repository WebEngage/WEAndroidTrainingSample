package com.webengage.demo.shopping;

import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.webengage.personalization.callbacks.WECampaignCallback;
import com.webengage.personalization.data.WECampaignData;
import com.webengage.sdk.android.LocationTrackingStrategy;
import com.webengage.sdk.android.PushChannelConfiguration;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.WebEngageConfig;
import com.webengage.sdk.android.actions.database.ReportingStrategy;
import com.webengage.sdk.android.actions.render.InAppNotificationData;
import com.webengage.sdk.android.actions.render.PushNotificationData;
import com.webengage.sdk.android.callbacks.CustomPushRender;
import com.webengage.sdk.android.callbacks.CustomPushRerender;
import com.webengage.sdk.android.callbacks.InAppNotificationCallbacks;
import com.webengage.sdk.android.callbacks.LifeCycleCallbacks;
import com.webengage.sdk.android.callbacks.PushNotificationCallbacks;
import com.webengage.sdk.android.callbacks.StateChangeCallbacks;
import com.webengage.sdk.android.callbacks.WESecurityCallback;
import com.webengage.personalization.WEPersonalization;

import java.lang.reflect.Method;
import java.util.Map;

public class WebEngageManager extends StateChangeCallbacks implements LifeCycleCallbacks, PushNotificationCallbacks, InAppNotificationCallbacks, CustomPushRender, CustomPushRerender, WESecurityCallback, WECampaignCallback {


    public static WebEngageConfig buildWebEngageConfig(String license) {
        NotificationManager mNotificationManager = (NotificationManager) ShoppingApplication.Companion.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannelGroup(new NotificationChannelGroup("test-group", "test-group"));
        }

        PushChannelConfiguration pushChannelConfiguration = new PushChannelConfiguration.Builder()
                .setNotificationChannelID("test-id")
                .setNotificationChannelName("test")
                .setNotificationChannelGroup("test-group")
                .setNotificationChannelImportance(NotificationManager.IMPORTANCE_DEFAULT)
                .setNotificationChannelDescription("test notification channel")
                .setNotificationChannelSound("light")
                .setNotificationChannelLightColor(Color.RED)
                .setNotificationChannelVibration(true)
                .setNotificationChannelShowBadge(true)
                .build();

        WebEngageConfig.Builder builder = new WebEngageConfig.Builder()
                .setWebEngageKey(license)
                .setDebugMode(true)
                .setAutoGCMRegistrationFlag(false)
//                .setEventReportingStrategy(ReportingStrategy.BUFFER)
//                .setPushLargeIcon(R.mipmap.ic_launcher)
//                .setPushAccentColor(Color.parseColor("#FF0000"))
//                .setLocationTracking(true)
                .setSessionDestroyTime(40)
                .shouldEncryptUserStorage(true)
                .setEventReportingStrategy(ReportingStrategy.FORCE_SYNC)
                .setLocationTrackingStrategy(LocationTrackingStrategy.DISABLED)
                .setDefaultPushChannelConfiguration(pushChannelConfiguration)  // works only on devices >= oreo
                .setAutoGAIDTracking(false);

        return builder.build();
    }

    public static void registerCallbacks() {
        WebEngage.registerStateChangeCallback(new WebEngageManager());
        WebEngage.registerPushNotificationCallback(new WebEngageManager());
        WebEngage.registerInAppNotificationCallback(new WebEngageManager());
        WebEngage.registerLifeCycleCallback(new WebEngageManager());
        WebEngage.registerCustomPushRenderCallback(new WebEngageManager());
        WebEngage.registerCustomPushRerenderCallback(new WebEngageManager());
        WebEngage.registerWESecurityCallback(new WebEngageManager());
        WEPersonalization.Companion.get().init();
        WEPersonalization.Companion.get().registerWECampaignCallback(new WebEngageManager());
    }

    public static void setPushToken() {
        try {


            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    try {

                        String token = task.getResult();
                        Log.d(Constants.TAG, "FCM Reg ID: " + token);
                        SharedPrefsManager.get().put(Constants.FCM_REG_ID, token);
                        WebEngage.get().setRegistrationID(token);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(Constants.TAG, "Google Messaging Service plugin not applied: " + e.toString());
        }
    }

    @Override
    public boolean onRender(Context context, PushNotificationData pushNotificationData) {
        return false;
    }

    @Override
    public boolean onRerender(Context context, PushNotificationData pushNotificationData, Bundle bundle) {
        return false;
    }

    @Override
    public InAppNotificationData onInAppNotificationPrepared(Context context, InAppNotificationData inAppNotificationData) {
        return null;
    }

    @Override
    public void onInAppNotificationShown(Context context, InAppNotificationData inAppNotificationData) {

    }

    @Override
    public boolean onInAppNotificationClicked(Context context, InAppNotificationData inAppNotificationData, String s) {
        return false;
    }

    @Override
    public void onInAppNotificationDismissed(Context context, InAppNotificationData inAppNotificationData) {

    }

    @Override
    public void onGCMRegistered(Context context, String s) {

    }

    @Override
    public void onGCMMessageReceived(Context context, Intent intent) {

    }

    @Override
    public void onAppInstalled(Context context, Intent intent) {

    }

    @Override
    public void onAppUpgraded(Context context, int i, int i1) {

    }

    @Override
    public void onNewSessionStarted() {

    }

    @Override
    public PushNotificationData onPushNotificationReceived(Context context, PushNotificationData pushNotificationData) {
        return pushNotificationData;
    }

    @Override
    public void onPushNotificationShown(Context context, PushNotificationData pushNotificationData) {

    }

    @Override
    public boolean onPushNotificationClicked(Context context, PushNotificationData pushNotificationData) {
        return false;
    }

    @Override
    public void onPushNotificationDismissed(Context context, PushNotificationData pushNotificationData) {

    }

    @Override
    public boolean onPushNotificationActionClicked(Context context, PushNotificationData pushNotificationData, String s) {
        return false;
    }

    @Override
    public void onSecurityException(Map<String, Object> map) {

    }

    @Override
    public void onCampaignException(@Nullable String s, @NonNull String s1, @NonNull Exception e) {

    }

    @Override
    public boolean onCampaignClicked(@NonNull String s, @NonNull String s1, @NonNull WECampaignData weCampaignData) {
        return false;
    }

    @Nullable
    @Override
    public WECampaignData onCampaignPrepared(@NonNull WECampaignData weCampaignData) {
        return weCampaignData;
    }

    @Override
    public void onCampaignShown(@NonNull WECampaignData weCampaignData) {

    }
}
