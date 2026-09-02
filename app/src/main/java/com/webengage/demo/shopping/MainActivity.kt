package com.webengage.demo.shopping

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.webengage.demo.shopping.Constants.accountTAG
import com.webengage.demo.shopping.Constants.cardsTAG
import com.webengage.demo.shopping.Constants.homeTAG
import com.webengage.demo.shopping.view.account.AccountFragment
import com.webengage.demo.shopping.view.cards.CardsFlowFragment
import com.webengage.demo.shopping.view.home.HomeFragment
import com.webengage.sdk.android.WebEngage

/**
 * Post-login shell hosting a BottomNavigationView with 3 tabs: Home, Cards, Account.
 * - The Cards tab defaults to Card Listing and owns its own push back stack
 *   (Card Detail / Card Application are pushed inside [CardsFlowFragment], NOT as
 *   separate bottom-nav destinations).
 * - Login is a separate Activity, so this shell never shows on the login screen.
 */
class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val cardsFragment = CardsFlowFragment()
    private val accountFragment = AccountFragment()
    private lateinit var bottomNavigationView: BottomNavigationView

    private val PUSH_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"

    private val weAnalytics = WebEngage.get().analytics()

    /**
     * Set when a Home inline CTA is deep-linking into Card Detail, so the Cards-tab
     * selection listener does NOT reset the flow back to the listing root.
     */
    private var deepLinkingToDetail = false

    private val bottomNavigationSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    loadFragment(homeTAG)
                    weAnalytics.screenNavigated("Home")
                }
                R.id.action_cards -> {
                    loadFragment(cardsTAG)
                    if (deepLinkingToDetail) {
                        // Coming from a Home CTA -> keep the deep-linked Card Detail.
                        deepLinkingToDetail = false
                    } else {
                        // Tapping the Cards tab always lands on the Card Listing root.
                        cardsFragment.popToRoot()
                        weAnalytics.screenNavigated("Card Listing")
                    }
                }
                R.id.action_account -> {
                    loadFragment(accountTAG)
                    weAnalytics.screenNavigated("Account")
                }
            }
            true
        }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(bottomNavigationSelectedListener)
        if (savedInstanceState == null) {
            loadFragment(homeTAG)
            weAnalytics.screenNavigated("Home")
        }
        checkForPushPermission()
    }

    /**
     * Deep-link entry point used by the Home inline CTAs: switch to the Cards tab
     * (which defaults to Card Listing) and open Card Detail.
     */
    fun openCardsFlowDetail(cardId: String) {
        // Stash the deep-link target first; CardsFlowFragment opens Card Detail once
        // its view is attached (it may not be attached yet when we switch tabs).
        deepLinkingToDetail = true
        cardsFragment.openCardDetail(cardId)
        // Switching the selected tab triggers the nav listener -> loadFragment(cardsTAG).
        bottomNavigationView.selectedItemId = R.id.action_cards
    }

    /** Reset the Cards tab back to its Card Listing root. */
    fun resetCardsFlow() {
        cardsFragment.popToRoot()
    }

    /** Programmatically move to the Cards tab (lands on Card Listing root). */
    fun selectCardsTab() {
        bottomNavigationView.selectedItemId = R.id.action_cards
    }

    /** Programmatically move to the Account tab. */
    fun selectAccountTab() {
        bottomNavigationView.selectedItemId = R.id.action_account
    }

    private fun checkForPushPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(PUSH_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(PUSH_NOTIFICATIONS), 102)
                WebEngage.get().user().setDevicePushOptIn(false)
            } else {
                WebEngage.get().user().setDevicePushOptIn(true)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(PUSH_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
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
                homeTAG -> homeFragment
                cardsTAG -> cardsFragment
                accountTAG -> accountFragment
                else -> throw IllegalArgumentException("Unknown tag: $fragmentTag")
            }
            fragmentTransaction.add(R.id.fragment_container, newFragment, fragmentTag)
        }
        fragmentTransaction.commit()
        Log.d(Constants.TAG, "loadFragment: $fragmentTag")
    }
}
