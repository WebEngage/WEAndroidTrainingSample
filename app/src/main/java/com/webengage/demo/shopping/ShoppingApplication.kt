package com.webengage.demo.shopping

import android.app.Application
import android.content.Context

class ShoppingApplication : Application() {
    private var mContext: Context? = null
    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext
    }

    fun getAppContext(): Context? {
        //Log.d("ShoppingApplication", "getAppContext mContext == null " + (ShoppingApplication.mContext == null) );
        return mContext
    }

}