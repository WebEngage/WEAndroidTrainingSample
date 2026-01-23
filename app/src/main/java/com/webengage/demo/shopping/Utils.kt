package com.webengage.demo.shopping

import android.content.Context
import android.widget.Toast

object Utils {

    fun showToast(message: String?) {
        ShoppingApplication.getAppContext()?.let { context ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    fun isBlank(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' } == ""
    }
}