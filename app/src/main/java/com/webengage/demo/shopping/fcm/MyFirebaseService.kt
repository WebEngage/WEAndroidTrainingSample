package com.webengage.demo.shopping.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.webengage.sdk.android.WebEngage

class MyFirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        if (data != null) {
            if (data.containsKey("source") && "webengage" == data["source"]) {
                WebEngage.get().receive(data)
            }
        } else {

        }
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        WebEngage.get().setRegistrationID(s)
    }
}
