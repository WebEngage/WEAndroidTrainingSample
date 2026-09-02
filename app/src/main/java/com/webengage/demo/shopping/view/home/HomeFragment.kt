package com.webengage.demo.shopping.view.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.Constants
import com.webengage.demo.shopping.MainActivity
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SessionManager
import com.webengage.personalization.WEInlineView
import com.webengage.personalization.WEPersonalization
import com.webengage.personalization.callbacks.WECampaignCallback
import com.webengage.personalization.callbacks.WEPlaceholderCallback
import com.webengage.personalization.data.WECampaignData
import com.webengage.sdk.android.WebEngage

/**
 * Home screen:
 * - "Welcome to Visa" hero with the session username (generic gradient artwork).
 * - A SINGLE inline slot (property id [Constants.PLACEMENT_PROMOTION] = "visa_promotion").
 *   The dashboard decides which campaign renders here: the Promotion campaign shows
 *   first, and the Abandonment campaign replaces it once Segment B qualifies. The app
 *   uses one WEInlineView for both.
 * - A fallback view is shown when no campaign is rendered, and hidden once the real
 *   inline renders.
 *
 * card_promotion_visited fires on CLICK of the inline CTA (not on load), via the
 * WECampaignCallback.onCampaignClicked hook. The CTA deep-links into the Cards flow.
 */
class HomeFragment : Fragment(), WECampaignCallback {

    private val weAnalytics = WebEngage.get().analytics()
    private lateinit var inlineView: WEInlineView
    private lateinit var promotionFallback: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = SessionManager.getUsername()
        // Show the first name/handle as the greeting headline (matches the web design).
        val firstName = username.trim().substringBefore(" ").ifBlank { "there" }
        view.findViewById<TextView>(R.id.heroUsername).text = firstName

        inlineView = view.findViewById(R.id.inlineView)
        promotionFallback = view.findViewById(R.id.promotionFallback)

        // Navigate to the Cards tab from the quick-action, CTA banner, fallback CTA
        // and the top-nav "Card" link.
        val goToCards = View.OnClickListener {
            (activity as? MainActivity)?.selectCardsTab()
        }
        view.findViewById<View>(R.id.quickExploreCards).setOnClickListener(goToCards)
        view.findViewById<View>(R.id.browseCardsButton).setOnClickListener(goToCards)
        view.findViewById<View>(R.id.promotionFallbackCta).setOnClickListener(goToCards)
        view.findViewById<View>(R.id.navCard).setOnClickListener(goToCards)
        view.findViewById<View>(R.id.navAccount).setOnClickListener {
            (activity as? MainActivity)?.selectAccountTab()
        }
    }

    override fun onStart() {
        super.onStart()
        weAnalytics.screenNavigated("Home")
        // Listen for campaign clicks so we can fire card_promotion_visited and deep-link.
        WEPersonalization.get().registerWECampaignCallback(this)
        loadInlineView()
    }

    private fun loadInlineView() {
        // Start with the fallback visible; hide it only once a real campaign renders.
        promotionFallback.visibility = View.VISIBLE
        inlineView.load(Constants.PLACEMENT_PROMOTION, object : WEPlaceholderCallback {
            override fun onDataReceived(data: WECampaignData) {
                Log.d(Constants.TAG, "Inline onDataReceived: ${data.targetViewId} campaign=${data.campaignId}")
            }

            override fun onPlaceholderException(
                campaignId: String?,
                targetViewId: String,
                error: Exception
            ) {
                // No campaign / resource failure -> keep the fallback view visible.
                Log.d(Constants.TAG, "Inline empty/exception: $targetViewId ${error.message}")
                activity?.runOnUiThread { promotionFallback.visibility = View.VISIBLE }
            }

            override fun onRendered(data: WECampaignData) {
                // A real campaign (promotion or abandonment) rendered -> hide the fallback.
                Log.d(Constants.TAG, "Inline onRendered: ${data.targetViewId} campaign=${data.campaignId}")
                activity?.runOnUiThread { promotionFallback.visibility = View.GONE }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        WEPersonalization.get().unregisterWECampaignCallback(this)
    }

    // --- WECampaignCallback ---

    override fun onCampaignClicked(
        actionId: String,
        deepLink: String,
        data: WECampaignData
    ): Boolean {
        Log.d(Constants.TAG, "onCampaignClicked: view=${data.targetViewId} campaign=${data.campaignId} deepLink=$deepLink")

        // Fire card_promotion_visited on click of the inline CTA.
        weAnalytics.track(Constants.EVENT_CARD_PROMOTION_VISITED)

        // The CTA deep-links into the Cards flow (Card Listing -> Card Detail).
        (activity as? MainActivity)?.openCardsFlowDetail(deepLinkToCardId(deepLink))

        // We've handled navigation; tell the SDK not to also process the deep link.
        return true
    }

    /**
     * The dashboard deep link may carry a card id (e.g. "premiumbank://cards/card_1").
     * Fall back to the flagship card if none is present.
     */
    private fun deepLinkToCardId(deepLink: String): String {
        val fromLink = deepLink.substringAfterLast("/", "").takeIf { it.startsWith("card_") }
        return fromLink ?: "card_1"
    }

    override fun onCampaignException(campaignId: String?, targetViewId: String, error: Exception) {}

    override fun onCampaignPrepared(data: WECampaignData): WECampaignData = data

    override fun onCampaignShown(data: WECampaignData) {}
}
