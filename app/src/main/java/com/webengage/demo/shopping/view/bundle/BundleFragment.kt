package com.webengage.demo.shopping.view.bundle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SharedPrefsManager
import com.webengage.sdk.android.WebEngage

class BundleFragment : Fragment() {

    private val weAnalytics = WebEngage.get().analytics()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bundle, container, false)
        weAnalytics.screenNavigated("Bundle Screen")
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        val usernameText = view.findViewById<TextView>(R.id.usernameText)
        val storedUserName = SharedPrefsManager.get().getString(SharedPrefsManager.USERNAME, "")
        usernameText.text = if (storedUserName.isNullOrEmpty()) "Guest User" else storedUserName

        view.findViewById<ImageView>(R.id.backIcon).setOnClickListener { 
            Log.d("BundleFragment", "Back Clicked")
            (activity as? com.webengage.demo.shopping.MainActivity)?.supportFragmentManager?.popBackStack()
        }
        view.findViewById<ImageView>(R.id.profileIcon).setOnClickListener { 
            Log.d("BundleFragment", "Profile Clicked")
            parentFragmentManager.beginTransaction()
                .replace(com.webengage.demo.shopping.R.id.fragment_container, com.webengage.demo.shopping.view.profile.ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<ImageView>(R.id.searchIcon).setOnClickListener { Log.d("BundleFragment", "Search Clicked") }
        view.findViewById<ImageView>(R.id.notificationIcon).setOnClickListener { Log.d("BundleFragment", "Notification Clicked") }
        view.findViewById<ImageView>(R.id.logoutIcon).setOnClickListener { 
            Log.d("BundleFragment", "Logout Clicked")
            SharedPrefsManager.get().put(SharedPrefsManager.USERNAME, "")
            WebEngage.get().user().logout()
            (activity as? com.webengage.demo.shopping.MainActivity)?.let { mainActivity ->
                mainActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(com.webengage.demo.shopping.R.id.bottom_navigation)?.visibility = View.GONE
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(com.webengage.demo.shopping.R.id.fragment_container, com.webengage.demo.shopping.view.user.UserFragment())
                    .commit()
            }
        }
    }
}
