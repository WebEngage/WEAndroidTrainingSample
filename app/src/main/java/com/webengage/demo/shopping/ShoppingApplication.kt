package com.webengage.demo.shopping

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.webengage.personalization.WEPersonalization
import com.webengage.personalization.callbacks.WECampaignCallback
import com.webengage.personalization.data.WECampaignData
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks
import com.webengage.sdk.android.actions.render.InAppNotificationData
import com.webengage.sdk.android.actions.render.PushNotificationData
import com.webengage.sdk.android.callbacks.InAppNotificationCallbacks
import com.webengage.sdk.android.callbacks.PushNotificationCallbacks
import org.json.JSONException
import org.json.JSONObject

class ShoppingApplication : Application(), PushNotificationCallbacks, WECampaignCallback,
    InAppNotificationCallbacks {


    private var mSharedPrefsManager: SharedPrefsManager? = null
    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
        initSharedPrefs()
        initWebEngage()
        passFCMTokenToWE()
        WebEngage.registerPushNotificationCallback(this)
        WEPersonalization.get().init()
        WEPersonalization.get().registerWECampaignCallback(this)
        WebEngage.registerInAppNotificationCallback(this)
        createChannel("alert_meme", "alert_meme")
        createChannel("emergency_alert", "emergency_alert")
    }

    // Creates custom channel with different sound set to each channel
    // If you want to add new channel add the sound in the raw folder and then update same in when() in below method
    private fun createChannel(channelName: String, soundName: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelName, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val alertSound = when(soundName) {
                "alert_meme" -> R.raw.alert_meme
                "emergency_alert" -> R.raw.emergency_alert
                else -> android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
            }

            val uri = if (alertSound is Int) {
                Uri.parse("android.resource://$packageName/$alertSound")
            } else {
                alertSound as Uri
            }
            notificationChannel.setSound(uri, attributes)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun initSharedPrefs() {
        mSharedPrefsManager = SharedPrefsManager.get()
    }

    private fun initWebEngage() {
        mSharedPrefsManager!!.put(Constants.WEBENGAGE_ENGAGED, false)
        val licenseCode: String = mSharedPrefsManager!!.getString(Constants.LICENSE_CODE, "")
        if (!Utils.isBlank(licenseCode)) {
            Log.d(Constants.TAG, "license code: $licenseCode")
            WebEngageManager.registerCallbacks()
            val webEngageConfig = WebEngageManager.buildWebEngageConfig(licenseCode)
            this.registerActivityLifecycleCallbacks(
                WebEngageActivityLifeCycleCallbacks(mContext, webEngageConfig)
            )
            //WebEngageManager.engage(mContext, webEngageConfig);
            WebEngageManager.setPushToken()
            val cuid: String = mSharedPrefsManager!!.getString(Constants.CUID, "")
            if (!Utils.isBlank(cuid)) {
                WebEngage.get().user().login(cuid, null)
            }
            mSharedPrefsManager!!.put(Constants.LICENSE_CODE, licenseCode)
            mSharedPrefsManager!!.put(Constants.WEBENGAGE_ENGAGED, true)
        }

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
    ): InAppNotificationData? {
        Log.d("TAG", "Inapp callback onInAppNotificationPrepared: $p1")
        return p1
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
        Log.d(
            "",
            "Inapp callback > onInAppNotificationClicked() > notification data json object: " + jsonObject.toString()
        )
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
        pushData: PushNotificationData
    ): PushNotificationData {
        Log.d("TAG", "onPushNotificationReceived: ${pushData.pushPayloadJSON}")
        if(pushData != null && pushData.customData != null && !pushData.customData.getString("channel").isNullOrEmpty()) {
            pushData.channelId = pushData.customData.getString("channel")
        }
        return pushData
    }

    override fun onPushNotificationShown(p0: Context?, p1: PushNotificationData) {
        Log.d("TAG", "onPushNotificationShown: ${p1.pushPayloadJSON}")
    }

    override fun onPushNotificationClicked(p0: Context?, p1: PushNotificationData): Boolean {
        Log.d(
            "TAG",
            "onPushNotificationClicked: ${p1.pushPayloadJSON} ${p1.primeCallToAction.action}"
        )
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
        Log.d(
            "TAG",
            "onPushNotificationActionClicked: button/action: $p2 link: ${p1.getCallToActionById(p2)} "
        )
        return false
    }

    companion object {
        private var mContext: Context? = null
        fun getAppContext(): Context? {
            return mContext
        }
    }
}