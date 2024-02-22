package com.webengage.demo.shopping

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.webengage.sdk.android.PushChannelConfiguration
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks
import com.webengage.sdk.android.WebEngageConfig
import com.webengage.sdk.android.actions.render.PushNotificationData
import com.webengage.sdk.android.callbacks.PushNotificationCallbacks

class ShoppingApplication : Application(), PushNotificationCallbacks {
    private var mContext: Context? = null
    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
        initWebEngage()
        passFCMTokenToWE()
        WebEngage.registerPushNotificationCallback(this)
    }

    private fun initWebEngage() {
        val webEngageConfig = WebEngageConfig.Builder()
            .setPushSmallIcon(R.mipmap.ic_launcher)
            .setPushLargeIcon(R.mipmap.ic_launcher_round)
            .setWebEngageKey("~2024b713")
            .setDebugMode(true) // only in development mode
            .build()
        registerActivityLifecycleCallbacks(
            WebEngageActivityLifeCycleCallbacks(
                this,
                webEngageConfig
            )
        )
    }

    private fun passFCMTokenToWE() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            try {
                val token: String? = task.result
                WebEngage.get().setRegistrationID(token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAppContext(): Context? {
        return mContext
    }

    override fun onPushNotificationReceived(
        p0: Context?,
        p1: PushNotificationData
    ): PushNotificationData {
        Log.d("TAG", "onPushNotificationReceived: ${p1.pushPayloadJSON}")
        return p1
    }

    override fun onPushNotificationShown(p0: Context?, p1: PushNotificationData) {
        Log.d("TAG", "onPushNotificationShown: ${p1.pushPayloadJSON}")
    }

    override fun onPushNotificationClicked(p0: Context?, p1: PushNotificationData): Boolean {
        Log.d("TAG", "onPushNotificationClicked: ${p1.pushPayloadJSON} ${p1.primeCallToAction.action}")
        return false
    }

    override fun onPushNotificationDismissed(p0: Context?, p1: PushNotificationData) {
        Log.d("TAG", "onPushNotificationDismissed: ${p1.pushPayloadJSON}")
    }

    override fun onPushNotificationActionClicked(
        p0: Context?,
        p1: PushNotificationData,
        p2: String?
    ): Boolean {
        Log.d("TAG", "onPushNotificationActionClicked: button/action: $p2 link: ${p1.getCallToActionById(p2)} ")
        return false
    }
}