package com.webengage.demo.shopping.view.offers

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SharedPrefsManager
import com.webengage.personalization.WEPersonalization
import com.webengage.personalization.callbacks.WEPlaceholderCallback
import com.webengage.personalization.data.WECampaignData
import com.webengage.sdk.android.WebEngage

class OffersFragment : Fragment(), WEPlaceholderCallback {

    private val weAnalytics = WebEngage.get().analytics()
    private lateinit var offersContainer: LinearLayout
    private lateinit var noOfferText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_offers, container, false)
        offersContainer = view.findViewById(R.id.offersContainer)
        noOfferText = view.findViewById(R.id.noOfferText)
        initViews(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        WEPersonalization.get().registerWEPlaceholderCallback("offer1", this)
    }

    override fun onStop() {
        super.onStop()
        WEPersonalization.get().unregisterWEPlaceholderCallback("offer1")
    }

    private fun initViews(view: View) {
        val usernameText = view.findViewById<TextView>(R.id.usernameText)
        val storedUserName = SharedPrefsManager.get().getString(SharedPrefsManager.USERNAME, "")
        usernameText.text = if (storedUserName.isNullOrEmpty()) "Guest User" else storedUserName

        view.findViewById<ImageView>(R.id.profileIcon).setOnClickListener { 
            Log.d("OffersFragment", "Profile Clicked")
            parentFragmentManager.beginTransaction()
                .replace(com.webengage.demo.shopping.R.id.fragment_container, com.webengage.demo.shopping.view.profile.ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<ImageView>(R.id.searchIcon).setOnClickListener { Log.d("OffersFragment", "Search Clicked") }
        view.findViewById<ImageView>(R.id.notificationIcon).setOnClickListener { Log.d("OffersFragment", "Notification Clicked") }
        view.findViewById<ImageView>(R.id.logoutIcon).setOnClickListener { 
            Log.d("OffersFragment", "Logout Clicked")
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

    override fun onDataReceived(data: WECampaignData) {
        Log.d("WebEngage", "onDataReceived: ${data.targetViewId}")
        Log.d("WebEngage", "onDataReceived1: ${data.toString()}")
        try {
            val customData = data.content?.customData
            if (customData != null && customData.isNotEmpty()) {
                noOfferText.visibility = View.GONE
                offersContainer.visibility = View.VISIBLE
                renderOffers(customData)
                createOfferCard("Special Offer", "Limited time", "50")
            }
        } catch (e: Exception) {
            Log.e("OffersFragment", "Error parsing offers: ${e.message}")
        }
    }

    override fun onPlaceholderException(campaignId: String?, targetViewId: String, error: Exception) {
        Log.e("OffersFragment", "Placeholder exception: ${error.message}")
    }

    override fun onRendered(data: WECampaignData) {
        Log.d("OffersFragment", "Inline content rendered")
    }



    private fun createOfferCard(title: String, validity: String, price: String) {
        val cardView = CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                resources.displayMetrics.widthPixels * 2 / 5,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 32, 0)
            }
            radius = 16f
            cardElevation = 4f
        }

        val contentLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#6FCF97"))
        }

        val titleText = TextView(requireContext()).apply {
            text = title
            textSize = 24f
            setTextColor(Color.WHITE)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val detailsText = TextView(requireContext()).apply {
            text = "$validity    ৳$price"
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(0, 16, 0, 0)
        }

        contentLayout.addView(titleText)
        contentLayout.addView(detailsText)
        cardView.addView(contentLayout)
        offersContainer.addView(cardView)
    }

    private fun renderOffers(customData: Map<String, Any>) {
        val offer1Title = customData["offer1_title"] as? String ?: ""
        val offer1Validity = customData["offer1_validity"] as? String ?: ""
        val offer1Price = customData["offer1_price"]?.toString() ?: ""
        createOfferCard(offer1Title, offer1Validity, offer1Price)

        val offer2Title = customData["offer2_title"] as? String ?: ""
        val offer2Validity = customData["offer2_validity"] as? String ?: ""
        val offer2Price = customData["offer2_price"]?.toString() ?: ""
        createOfferCard(offer2Title, offer2Validity, offer2Price)
    }
}
