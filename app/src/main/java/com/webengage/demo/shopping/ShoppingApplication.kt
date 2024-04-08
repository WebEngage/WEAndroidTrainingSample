package com.webengage.demo.shopping

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.webengage.personalization.WEPersonalization
import com.webengage.personalization.callbacks.WECampaignCallback
import com.webengage.personalization.data.WECampaignData
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks
import com.webengage.sdk.android.WebEngageConfig
import com.webengage.sdk.android.actions.render.InAppNotificationData
import com.webengage.sdk.android.actions.render.PushNotificationData
import com.webengage.sdk.android.callbacks.InAppNotificationCallbacks
import com.webengage.sdk.android.callbacks.PushNotificationCallbacks
import org.json.JSONException
import org.json.JSONObject

class ShoppingApplication : Application(), PushNotificationCallbacks, WECampaignCallback , InAppNotificationCallbacks {
    private var mContext: Context? = null
    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
        initWebEngage()
        passFCMTokenToWE()
        WebEngage.registerPushNotificationCallback(this)
        WEPersonalization.get().init()
        WEPersonalization.get().registerWECampaignCallback(this)
        WebEngage.registerInAppNotificationCallback(this)
    }

    private fun initWebEngage() {
        val webEngageConfig = WebEngageConfig.Builder()
            .setPushSmallIcon(R.mipmap.ic_launcher)
            .setPushLargeIcon(R.mipmap.ic_launcher_round)
            .setWebEngageKey("WEBENGAGE_KEY")
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

    override fun onCampaignClicked(
        actionId: String,
        deepLink: String,
        data: WECampaignData
    ): Boolean {
        return false
    }

    override fun onCampaignException(campaignId: String?, targetViewId: String, error: Exception) {
    }

    override fun onCampaignPrepared(data: WECampaignData): WECampaignData? {
        return data
    }

    override fun onCampaignShown(data: WECampaignData) {

    }

    override fun onInAppNotificationPrepared(
        p0: Context?,
        p1: InAppNotificationData?
    ): InAppNotificationData {
        Log.d("TAG", "Inapp callback onInAppNotificationPrepared: $p1")
        return p1!!
    }

    override fun onInAppNotificationShown(p0: Context?, p1: InAppNotificationData?) {
        Log.d("TAG", "Inapp callback onInAppNotificationShown: $p1")
    }

    override fun onInAppNotificationClicked(
        p0: Context?,
        inAppNotificationData: InAppNotificationData,
        p2: String?
    ): Boolean {
        Log.d("TAG", "Inapp callback onInAppNotificationClicked: $inAppNotificationData ")
        val jsonObject: JSONObject = inAppNotificationData.getData()
        Log.d("", "Inapp callback > onInAppNotificationClicked() > notification data json object: " + jsonObject.toString())
        try {
            val actions =
                if (jsonObject.isNull("actions")) null else jsonObject.getJSONArray("actions")
            if (actions != null) {
                var actionLink: String? = null
                for (i in 0 until actions.length()) {
                    val action = actions.getJSONObject(i)
                    val actionEId =
                        if (action.isNull("actionEId")) null else action.optString("actionEId")
                    if (actionEId != null && actionEId == p2) {
                        actionLink =
                            if (action.isNull("actionLink")) null else action.getString("actionLink")
                        break
                    }
                }
                Log.d("", "Inapp callback action link: $actionLink")
            }
        } catch (e: JSONException) {
        } catch (t: Throwable) {
        }
        return false
    }

    override fun onInAppNotificationDismissed(p0: Context?, p1: InAppNotificationData?) {

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