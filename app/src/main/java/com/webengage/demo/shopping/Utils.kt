package com.webengage.demo.shopping

import android.widget.Toast

object Utils {

    fun showToast(message: String?) {
        Toast.makeText(ShoppingApplication.getAppContext(), message, Toast.LENGTH_LONG).show()
    }

    fun isBlank(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' } == ""
    }
}