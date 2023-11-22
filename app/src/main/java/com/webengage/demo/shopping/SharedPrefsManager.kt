package com.webengage.demo.shopping

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SharedPrefsManager {
    private val TAG = SharedPrefsManager::class.java.simpleName

    lateinit var sharedPref: SharedPreferences
    val USERNAME = "username"

    private fun initialize(context: Context) {
        sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    }

    fun get(context: Context): SharedPreferences{
        if(::sharedPref.isInitialized ){
            return sharedPref
        }
        initialize(context)
        return sharedPref

    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    fun putString(key: String, value: String) {
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun remove(key: String) {
        val editor = sharedPref.edit()
        editor.remove(key)
        editor.apply()
    }

    fun contains(key: String): Boolean {
        return sharedPref.contains(key)
    }

    fun clear() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}