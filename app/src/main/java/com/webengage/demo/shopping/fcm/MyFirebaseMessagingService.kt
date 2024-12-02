package com.webengage.demo.shopping.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.webengage.sdk.android.WebEngage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        if (data != null) {
            Log.i("SHOPPING","data $data")
            if (data.containsKey("source") && "webengage" == data["source"]) {
                WebEngage.get().receive(data)
            }
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("SHOPPING", "FCM TOKEN $token")
        WebEngage.get().setRegistrationID(token)

    }
}