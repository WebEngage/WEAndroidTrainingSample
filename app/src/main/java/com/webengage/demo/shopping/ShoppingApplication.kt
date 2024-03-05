package com.webengage.demo.shopping

import android.app.Application
import android.content.Context
import android.util.Log
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks
import com.webengage.sdk.android.WebEngageConfig
import com.webengage.sdk.android.actions.render.InAppNotificationData
import com.webengage.sdk.android.callbacks.InAppNotificationCallbacks
import org.json.JSONException
import org.json.JSONObject

class ShoppingApplication : Application(), InAppNotificationCallbacks {
    private var mContext: Context? = null
    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
        initWebEngage()
        WebEngage.registerInAppNotificationCallback(this)
    }

    private fun initWebEngage() {
        val webEngageConfig = WebEngageConfig.Builder()
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

    fun getAppContext(): Context? {
        return mContext
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
}