package com.webengage.demo.shopping

/**
 * Lightweight, foreground-only mock session for the credit-card cross-sell app.
 * Backed by SharedPrefsManager so the session survives configuration changes
 * within an app session. There is no real backend or auth.
 */
object SessionManager {

    fun saveSession(username: String, tier: String) {
        val prefs = SharedPrefsManager.get()
        prefs.put(Constants.SESSION_USERNAME, username)
        prefs.put(Constants.SESSION_TIER, tier)
    }

    fun getUsername(): String = SharedPrefsManager.get().getString(Constants.SESSION_USERNAME, "")

    fun getTier(): String = SharedPrefsManager.get().getString(Constants.SESSION_TIER, "")

    fun isLoggedIn(): Boolean = getUsername().isNotBlank()

    fun clear() {
        SharedPrefsManager.get().remove(Constants.SESSION_USERNAME, Constants.SESSION_TIER)
    }
}
