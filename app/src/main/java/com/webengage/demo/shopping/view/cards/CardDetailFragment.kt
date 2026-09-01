package com.webengage.demo.shopping.view.cards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.Constants
import com.webengage.demo.shopping.R
import com.webengage.sdk.android.WebEngage

/**
 * Card Detail — renders a single static/hardcoded card's details, identical
 * regardless of which card was tapped in the listing (the card id is still passed
 * in for tracking/consistency). "Start Application" fires card_application_started
 * and pushes the Card Application screen.
 */
class CardDetailFragment : Fragment() {

    private val weAnalytics = WebEngage.get().analytics()

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
        val startButton = view.findViewById<Button>(R.id.startApplicationButton)
        startButton.setOnClickListener {
            // Fire the event first, then navigate on the next main-loop tick so the
            // track() call is fully dispatched before this fragment is torn down.
            Log.d(Constants.TAG, "Tracking event: ${Constants.EVENT_CARD_APPLICATION_STARTED}")
            WebEngage.get().analytics().track(Constants.EVENT_CARD_APPLICATION_STARTED)
            startButton.post {
                (parentFragment as? CardsFlowFragment)?.openCardApplication()
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
