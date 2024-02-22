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
import com.webengage.sdk.android.WebEngage

class MainActivity : AppCompatActivity(), FragmentListener {

    private val homeFragment = HomeProductsFragment()
    private val userFragment = UserFragment()
    private val cartFragment = CartFragment()
    lateinit var bottomNavigationView: BottomNavigationView
    private val PUSH_NOTIFICATIONS =
        "android.permission.POST_NOTIFICATIONS" //Applicable from Android 13 and above


    //private val cartList = mutableListOf<ProductCategory>()
    private val bottomNavigationSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> loadFragment(HomeProductsFragment.TAG)
                R.id.action_cart -> loadFragment(CartFragment.TAG)
                R.id.action_profile -> loadFragment(UserFragment.TAG)
            }
            true
        }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && windowInsetsController != null) {
            windowInsetsController.hide(WindowInsets.Type.statusBars())
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(bottomNavigationSelectedListener)
        loadFragment(HomeProductsFragment.TAG)
        checkForPushPermission()
    }

    private fun checkForPushPermission() {
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
    private fun loadFragment(fragmentTag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        for (fragment in fragmentManager.fragments) {
            fragmentTransaction.hide(fragment)
        }
        val existingFragment = fragmentManager.findFragmentByTag(fragmentTag)

        if (existingFragment != null) {
            fragmentTransaction.show(existingFragment)
        } else {
            val newFragment = when (fragmentTag) {
                HomeProductsFragment.TAG -> homeFragment
                UserFragment.TAG -> userFragment
                CartFragment.TAG -> cartFragment
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
        if (actionType.equals(HomeProductsFragment.TAG)) {
            loadFragment(actionType)
        }
    }



}
