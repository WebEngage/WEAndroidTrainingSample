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
        initWebEngage()
    }

    private fun initWebEngage() {
        val webEngageConfig = WebEngageConfig.Builder()
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

    fun getAppContext(): Context? {
        return mContext
    }
}