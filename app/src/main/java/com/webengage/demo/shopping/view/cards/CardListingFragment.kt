package com.webengage.demo.shopping.view.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webengage.demo.shopping.R
import com.webengage.sdk.android.WebEngage

/**
 * Card Listing — the root of the Cards tab. Parses cards.json and renders a
 * 2-column grid (image, name, limit, short description). Tapping a card pushes
 * Card Detail onto the Cards flow back stack (passing the card id).
 */
class CardListingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_card_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.cardsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val cards = CardRepository.getCards(requireContext())
        recyclerView.adapter = CardAdapter(cards) { card ->
            (parentFragment as? CardsFlowFragment)?.openCardDetail(card.id)
        }
    }

    override fun onStart() {
        super.onStart()
        // Tag the screen on every landing (fresh or via back), per WebEngage docs.
        WebEngage.get().analytics().screenNavigated("Card Listing")
    }
}
