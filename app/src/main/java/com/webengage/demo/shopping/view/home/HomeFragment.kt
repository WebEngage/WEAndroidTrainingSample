package com.webengage.demo.shopping.view.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SharedPrefsManager
import com.webengage.demo.shopping.MainActivity
import com.webengage.sdk.android.WebEngage

class HomeFragment : Fragment() {

    private val weAnalytics = WebEngage.get().analytics()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        val usernameText = view.findViewById<TextView>(R.id.usernameText)
        val storedUserName = SharedPrefsManager.get().getString(SharedPrefsManager.USERNAME, "")
        usernameText.text = if (storedUserName.isNullOrEmpty()) "Guest User" else storedUserName

        view.findViewById<ImageView>(R.id.profileIcon).setOnClickListener { 
            Log.d("HomeFragment", "Profile Clicked")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.webengage.demo.shopping.view.profile.ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<ImageView>(R.id.searchIcon).setOnClickListener { Log.d("HomeFragment", "Search Clicked") }
        view.findViewById<ImageView>(R.id.eventsIcon).setOnClickListener { 
            Log.d("HomeFragment", "Events Clicked")
            com.webengage.demo.shopping.view.events.EventsDialogFragment().show(parentFragmentManager, "EventsDialog")
        }
        view.findViewById<ImageView>(R.id.notificationIcon).setOnClickListener { Log.d("HomeFragment", "Notification Clicked") }
        view.findViewById<ImageView>(R.id.logoutIcon).setOnClickListener { 
            Log.d("HomeFragment", "Logout Clicked")
            SharedPrefsManager.get().put(SharedPrefsManager.USERNAME, "")
            WebEngage.get().user().logout()
            (activity as? MainActivity)?.let { mainActivity ->
                mainActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, com.webengage.demo.shopping.view.user.UserFragment())
                    .commit()
            }
        }

        view.findViewById<Button>(R.id.btnRecharge).setOnClickListener { 
            Log.d("HomeFragment", "Recharge Pressed")
            weAnalytics.track("Recharge Button")
            (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, com.webengage.demo.shopping.view.recharge.RechargeFragment())
                ?.commit()
        }
        view.findViewById<Button>(R.id.btnInternet).setOnClickListener { 
            Log.d("HomeFragment", "Internet Pressed")
            weAnalytics.track("Internet Button")
        }
        view.findViewById<Button>(R.id.btnMinutes).setOnClickListener { 
            Log.d("HomeFragment", "Minutes Pressed")
            weAnalytics.track("Minutes Button")
        }
        view.findViewById<Button>(R.id.btnBundles).setOnClickListener { 
            Log.d("HomeFragment", "Bundles Pressed")
            weAnalytics.track("Bundles Button")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.webengage.demo.shopping.view.bundle.BundleFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<Button>(R.id.btnEmergency).setOnClickListener { 
            Log.d("HomeFragment", "Emergency Pressed")
            weAnalytics.track("Emergency Button")
        }
        view.findViewById<Button>(R.id.btnBalanceTransfer).setOnClickListener { 
            Log.d("HomeFragment", "Balance Transfer Pressed")
            weAnalytics.track("Balance Transfer Button")
        }
        view.findViewById<Button>(R.id.btnMyOffers).setOnClickListener { 
            Log.d("HomeFragment", "My Offers Pressed")
            weAnalytics.track("My Offers Button")
        }
        view.findViewById<Button>(R.id.btnJob).setOnClickListener { 
            Log.d("HomeFragment", "Job Pressed")
            weAnalytics.track("Job Button")
        }
    }
}
