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
 * - Inline View #1 (Promotion, placement id [Constants.PLACEMENT_PROMOTION]) — renders
 *   unconditionally per dashboard config.
 * - Inline View #2 (Abandonment, placement id [Constants.PLACEMENT_ABANDONMENT]) — only
 *   renders when Segment B matches (handled by the SDK; no app-side condition).
 *
 * card_promotion_visited fires on CLICK of the Promotion block's CTA (not on load),
 * via the WECampaignCallback.onCampaignClicked hook. Both CTAs deep-link into the
 * Cards tab flow (Card Listing -> Card Detail).
 */
class HomeFragment : Fragment(), WECampaignCallback {

    private val weAnalytics = WebEngage.get().analytics()
    private lateinit var promotionInline: WEInlineView
    private lateinit var abandonmentInline: WEInlineView
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
        view.findViewById<TextView>(R.id.heroUsername).text =
            if (username.isNotBlank()) "Hello, $username" else "Hello"

        promotionInline = view.findViewById(R.id.promotionInline)
        abandonmentInline = view.findViewById(R.id.abandonmentInline)
        promotionFallback = view.findViewById(R.id.promotionFallback)

        // Navigate to the Cards tab from the quick-action, CTA banner, and fallback CTA.
        val goToCards = View.OnClickListener {
            (activity as? MainActivity)?.selectCardsTab()
        }
        view.findViewById<View>(R.id.quickExploreCards).setOnClickListener(goToCards)
        view.findViewById<View>(R.id.browseCardsButton).setOnClickListener(goToCards)
        view.findViewById<View>(R.id.promotionFallbackCta).setOnClickListener(goToCards)
    }

    override fun onStart() {
        super.onStart()
        weAnalytics.screenNavigated("Home")
        // Listen for campaign clicks so we can fire card_promotion_visited and deep-link.
        WEPersonalization.get().registerWECampaignCallback(this)
        loadInlineViews()
    }

    private fun loadInlineViews() {
        // Start with the fallback visible; hide it only once the real inline renders.
        promotionFallback.visibility = View.VISIBLE
        promotionInline.load(Constants.PLACEMENT_PROMOTION, object : WEPlaceholderCallback {
            override fun onDataReceived(data: WECampaignData) {
                Log.d(Constants.TAG, "Promotion inline onDataReceived: ${data.targetViewId}")
            }

            override fun onPlaceholderException(
                campaignId: String?,
                targetViewId: String,
                error: Exception
            ) {
                // No campaign / resource failure -> keep the fallback view visible.
                Log.d(Constants.TAG, "Promotion inline empty/exception: $targetViewId ${error.message}")
                activity?.runOnUiThread { promotionFallback.visibility = View.VISIBLE }
            }

            override fun onRendered(data: WECampaignData) {
                // Real personalized campaign rendered -> hide the fallback.
                Log.d(Constants.TAG, "Promotion inline onRendered: ${data.targetViewId}")
                activity?.runOnUiThread { promotionFallback.visibility = View.GONE }
            }
        })

        // Abandonment slot: the WEInlineView collapses on its own when there's no
        // campaign and expands when Segment B renders. Do NOT gate visibility here.
        abandonmentInline.load(Constants.PLACEMENT_ABANDONMENT, object : WEPlaceholderCallback {
            override fun onDataReceived(data: WECampaignData) {
                Log.d(Constants.TAG, "Abandonment inline onDataReceived: ${data.targetViewId}")
            }

            override fun onPlaceholderException(
                campaignId: String?,
                targetViewId: String,
                error: Exception
            ) {
                Log.d(Constants.TAG, "Abandonment inline empty/exception: $targetViewId ${error.message}")
            }

            override fun onRendered(data: WECampaignData) {
                Log.d(Constants.TAG, "Abandonment inline onRendered: ${data.targetViewId}")
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
        Log.d(Constants.TAG, "onCampaignClicked: view=${data.targetViewId} deepLink=$deepLink")

        // Fire card_promotion_visited ONLY for the Promotion block's CTA (on click).
        if (data.targetViewId == Constants.PLACEMENT_PROMOTION) {
            weAnalytics.track(Constants.EVENT_CARD_PROMOTION_VISITED)
        }

        // Both CTAs ("Apply" / "Continue Application") deep-link into the Cards flow.
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
