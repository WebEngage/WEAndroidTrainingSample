package com.webengage.demo.shopping

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.webengage.demo.shopping.Constants.homeTAG
import com.webengage.demo.shopping.Constants.offersTAG
import com.webengage.demo.shopping.Constants.rechargeTAG
import com.webengage.demo.shopping.Constants.userTAG
import com.webengage.demo.shopping.view.home.HomeFragment
import com.webengage.demo.shopping.view.offers.OffersFragment
import com.webengage.demo.shopping.view.recharge.RechargeFragment
import com.webengage.demo.shopping.view.user.UserFragment
import com.webengage.personalization.callbacks.WECampaignCallback
import com.webengage.personalization.data.WECampaignData
import com.webengage.sdk.android.WebEngage

class MainActivity : AppCompatActivity(), FragmentListener, WECampaignCallback {

    private val homeFragment = HomeFragment()
    private val offersFragment = OffersFragment()
    private val rechargeFragment = RechargeFragment()
    private val userFragment = UserFragment()
    private lateinit var bottomNavigationView: BottomNavigationView
    private val PUSH_NOTIFICATIONS =
        "android.permission.POST_NOTIFICATIONS" //Applicable from Android 13 and above

    private val weAnalytics = WebEngage.get().analytics()

    private val bottomNavigationSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    weAnalytics.screenNavigated("home")
                    weAnalytics.track("home_screen_viewed")
                    loadFragment(homeTAG, "HomeScreen")
                }

                R.id.action_offers -> {
                    weAnalytics.screenNavigated("offers")
                    weAnalytics.track("offers_viewed")
                    loadFragment(offersTAG, "OffersScreen")
                }

                R.id.action_recharge -> {
                    weAnalytics.screenNavigated("recharge")
                    weAnalytics.track("recharge_screen_viewed")

                    loadFragment(rechargeTAG, "RechargeScreen")
                }
            }
            true
        }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(bottomNavigationSelectedListener)
        
        val storedUserName = SharedPrefsManager.get().getString(SharedPrefsManager.USERNAME, "")
        if (storedUserName.isNullOrEmpty()) {
            bottomNavigationView.visibility = View.GONE
            loadFragment(userTAG, "UserProfile")
        } else {
            bottomNavigationView.visibility = View.VISIBLE
            loadFragment(homeTAG, "HomeScreen")
        }
    }

    fun checkForPushPermission() {
        //For App's targeting below 33
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= 33) {
                Log.d("",
                    "onResume: checking for PUSH_NOTIFICATIONS: " + (checkSelfPermission(PUSH_NOTIFICATIONS) === PackageManager.PERMISSION_GRANTED)
                )
                if (checkSelfPermission(PUSH_NOTIFICATIONS) !== PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf<String>(PUSH_NOTIFICATIONS),
                        102
                    )
                    WebEngage.get().user().setDevicePushOptIn(false)
                } else {
                    WebEngage.get().user().setDevicePushOptIn(true)
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(
            "",
            "onRequestPermissionsResult permissions: $permissions grantResults: $grantResults"
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(PUSH_NOTIFICATIONS) === PackageManager.PERMISSION_GRANTED) {
                WebEngage.get().user().setDevicePushOptIn(true)
            } else {
                WebEngage.get().user().setDevicePushOptIn(false)
            }
        }
    }
    private fun loadFragment(fragmentTag: String, screenName: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        
        // Clear back stack when navigating via bottom navigation
        fragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        
        for (fragment in fragmentManager.fragments) {
            fragmentTransaction.hide(fragment)
        }
        val existingFragment = fragmentManager.findFragmentByTag(fragmentTag)

        if (existingFragment != null) {
            fragmentTransaction.show(existingFragment)
        } else {
            val newFragment = when (fragmentTag) {
                homeTAG -> homeFragment
                offersTAG -> offersFragment
                rechargeTAG -> rechargeFragment
                userTAG -> userFragment
                else -> throw IllegalArgumentException("Unknown tag: $fragmentTag")
            }
            fragmentTransaction.add(R.id.fragment_container, newFragment, fragmentTag)
        }

        fragmentTransaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the selected item index in the BottomNavigationView
        outState.putInt("selectedItemId", bottomNavigationView.selectedItemId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the selected item index in the BottomNavigationView
        val selectedItemId = savedInstanceState.getInt("selectedItemId")
        bottomNavigationView.selectedItemId = selectedItemId
    }

    override fun onFragmentAction(actionType: String) {
        if (actionType == (homeTAG)) {
            loadFragment(actionType, "HomeScreen")
        }
    }

    override fun onCampaignClicked(
        actionId: String,
        deepLink: String,
        data: WECampaignData
    ): Boolean {
        Log.d("TAG", "onCampaignClicked: ")
        return false
    }

    override fun onCampaignException(campaignId: String?, targetViewId: String, error: Exception) {
    }

    override fun onCampaignPrepared(data: WECampaignData): WECampaignData? {
        return data
    }

    override fun onCampaignShown(data: WECampaignData) {

    }
}
