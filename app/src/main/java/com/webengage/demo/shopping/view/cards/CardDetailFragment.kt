package com.webengage.demo.shopping.view.cards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.Constants
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SessionManager
import com.webengage.sdk.android.WebEngage

/**
 * Card Detail — shows the specific tapped card (image, its own "What you get"
 * feature list and its own annual fee), mirroring the website's
 * /card-details/[cardId] page. When opened without a specific card (e.g. a Home
 * inline CTA) it defaults to the first card.
 *
 * "Start Application" fires card_application_started { cardId, tier }, sets the
 * lastStartedCardId user attribute, then pushes Card Application.
 */
class CardDetailFragment : Fragment() {

    private val weAnalytics = WebEngage.get().analytics()
    private var card: Card? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_card_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.backButton).setOnClickListener {
            (parentFragment as? CardsFlowFragment)?.goBack()
        }

        // Resolve the tapped card; default to the first card when none was passed.
        val cardId = arguments?.getString(ARG_CARD_ID)
        val cards = CardRepository.getCards(requireContext())
        val resolved = CardRepository.findById(requireContext(), cardId) ?: cards.firstOrNull()
        card = resolved

        resolved?.let { c ->
            CardImageLoader.load(view.findViewById(R.id.cardArt), c.imageRes)

            // Per-card "What you get" features.
            val featuresContainer = view.findViewById<LinearLayout>(R.id.featuresContainer)
            featuresContainer.removeAllViews()
            val inflater = LayoutInflater.from(requireContext())
            c.features.forEach { text ->
                val row = inflater.inflate(R.layout.item_feature, featuresContainer, false)
                row.findViewById<TextView>(R.id.featureText).text = text
                featuresContainer.addView(row)
            }

            // Per-card annual fee.
            view.findViewById<TextView>(R.id.annualFeeValue).text = c.annualFee
        }

        val startButton = view.findViewById<Button>(R.id.startApplicationButton)
        startButton.setOnClickListener {
            val current = card ?: return@setOnClickListener
            val attributes = mutableMapOf<String, Any>(
                "cardId" to current.id,
                "tier" to SessionManager.getTier()
            )
            Log.d(Constants.TAG, "Tracking event: ${Constants.EVENT_CARD_APPLICATION_STARTED} $attributes")
            WebEngage.get().analytics().track(Constants.EVENT_CARD_APPLICATION_STARTED, attributes)
            // So an Abandonment creative can link back to the right card.
            WebEngage.get().user().setAttribute("lastStartedCardId", current.id)
            startButton.post {
                (parentFragment as? CardsFlowFragment)?.openCardApplication(current.id)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Tag the screen on every landing (fresh or via back), per WebEngage docs.
        weAnalytics.screenNavigated("Card Detail")
    }

    companion object {
        const val ARG_CARD_ID = "card_id"
    }
}
