package com.webengage.demo.shopping

import android.app.Application
import android.content.Context
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks
import com.webengage.sdk.android.WebEngageConfig

class ShoppingApplication : Application() {
    private var mContext: Context? = null
    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
        val webEngageConfig = WebEngageConfig.Builder()
            .setWebEngageKey("YOUR_WEBENGAGE_LICENSE_CODE")
            .setDebugMode(true) // only in development mode
            .build()
        registerActivityLifecycleCallbacks(
            WebEngageActivityLifeCycleCallbacks(
                this,
                webEngageConfig
            ))

    }

    fun getAppContext(): Context? {
        return mContext
    }
}