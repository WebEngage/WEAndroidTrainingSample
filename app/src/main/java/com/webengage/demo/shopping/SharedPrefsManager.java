package com.webengage.demo.shopping;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.Set;

public class SharedPrefsManager {
    private static final String MY_PREFERENCES = "my_preferences";
    private static final String TAG = SharedPrefsManager.class.getSimpleName();
    private static Editor mEditor;
    private static SharedPreferences mSharedPrefs;
    private static SharedPrefsManager mSharedPrefsManager;
    public static String USERNAME = "username";

    private SharedPrefsManager(Context context) {
        mSharedPrefs = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
    }

    private static void initialize() {
        synchronized (SharedPrefsManager.class) {
            if (mSharedPrefsManager == null) {
                mSharedPrefsManager = new SharedPrefsManager(ShoppingApplication.Companion.getAppContext());
            }
        }
    }

    public static SharedPrefsManager get() {
        if (mSharedPrefsManager == null) {
            initialize();
        }
        return mSharedPrefsManager;
    }

    private void doEdit() {
        if (mEditor == null) {
            mEditor = mSharedPrefs.edit();
        }
    }

    private void doCommit() {
        if (mEditor != null) {
            mEditor.apply();
            mEditor = null;
        }
    }

    public boolean contains(String key) {
        return mSharedPrefs.contains(key);
    }

    public void put(String key, String value) {
        doEdit();
        Log.d(TAG , " > putString() > " + key + ": " + value);
        mEditor.putString(key, value).apply();
        doCommit();
    }

    public void put(String key, Set<String> value) {
        Log.d(TAG , " > putStringSet() > " + key + ": " + value);
        doEdit();
        mEditor.putStringSet(key, value);
        doCommit();
    }

    public void put(String key, boolean value) {
        doEdit();
        mEditor.putBoolean(key, value);
        doCommit();
    }

    public void put(String key, int value) {
        doEdit();
        mEditor.putInt(key, value);
        doCommit();
    }

    public void put(String key, long value) {
        doEdit();
        mEditor.putLong(key, value);
        doCommit();
    }

    public String getString(String key, String defaultValue) {
        return mSharedPrefs.getString(key, defaultValue);
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return mSharedPrefs.getStringSet(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPrefs.getBoolean(key, defaultValue);
    }

    public int getInteger(String key, int defaultValue) {
        return mSharedPrefs.getInt(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return mSharedPrefs.getLong(key, defaultValue);
    }

    public void remove(String... keys) {
        doEdit();
        for (String key : keys) {
            Log.d(TAG , " > remove() > key: " + key);
            mEditor.remove(key);
        }
        doCommit();
    }

    public void clear() {
        doEdit();
        Log.d(TAG , "clear()");
        mEditor.clear();
        doCommit();
    }
}
