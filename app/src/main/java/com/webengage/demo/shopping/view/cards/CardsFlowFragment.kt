package com.webengage.demo.shopping.view.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.webengage.demo.shopping.R

/**
 * Container for the Cards tab. Card Listing is the root; Card Detail and Card
 * Application are pushed onto this fragment's own (child) back stack, so they are
 * NOT separate bottom-nav destinations.
 *
 * A back-press callback pops this child back stack (Application -> Detail -> Listing)
 * before the Activity handles back, so the app doesn't close while inside the Cards
 * flow. The callback is only enabled while there is something to pop.
 */
class CardsFlowFragment : Fragment() {

    private var pendingCardId: String? = null

    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_cards_flow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)
        childFragmentManager.addOnBackStackChangedListener {
            backCallback.isEnabled = childFragmentManager.backStackEntryCount > 0
        }

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.cards_flow_container, CardListingFragment(), "listing")
                .commit()
        }
        // Flush any deep-link request that arrived before the view existed.
        pendingCardId?.let { id ->
            pendingCardId = null
            pushCardDetail(id)
        }
        // Sync initial state (e.g. after a config change with an existing back stack).
        backCallback.isEnabled = childFragmentManager.backStackEntryCount > 0
    }

    override fun onResume() {
        super.onResume()
        // While the Cards tab is visible, enable back handling if we have a stack.
        backCallback.isEnabled = childFragmentManager.backStackEntryCount > 0
    }

    /** Card Listing -> Card Detail (push). Safe to call before the view is attached. */
    fun openCardDetail(cardId: String) {
        if (isAdded && view != null) {
            pushCardDetail(cardId)
        } else {
            pendingCardId = cardId
        }
    }

    private fun pushCardDetail(cardId: String) {
        val detail = CardDetailFragment().apply {
            arguments = Bundle().apply { putString(CardDetailFragment.ARG_CARD_ID, cardId) }
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.cards_flow_container, detail, "detail")
            .addToBackStack("detail")
            .commit()
    }

    /** Card Detail -> Card Application (push). */
    fun openCardApplication() {
        childFragmentManager.beginTransaction()
            .replace(R.id.cards_flow_container, CardApplicationFragment(), "application")
            .addToBackStack("application")
            .commit()
    }

    /** Pop the top screen (used by the in-screen back arrow). */
    fun goBack() {
        if (isAdded && childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
        }
    }

    /** Pop everything back to the Card Listing root. */
    fun popToRoot() {
        if (isAdded) {
            childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}
