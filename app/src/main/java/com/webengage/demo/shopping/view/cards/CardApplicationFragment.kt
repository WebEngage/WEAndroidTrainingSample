package com.webengage.demo.shopping.view.cards

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.Constants
import com.webengage.demo.shopping.MainActivity
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SessionManager
import com.webengage.sdk.android.WebEngage

/**
 * Card Application — mirrors the website's /apply/[cardId] form: eyebrow with the
 * card name, six fields (Full name prefilled with the session name, Email, Phone
 * number, Monthly income, PAN, Address) and a mandatory "I agree…" checkbox. Submit
 * is disabled until the checkbox is ticked. On submit fires
 * card_application_completed { cardId, tier }, shows a success state, then returns
 * to the Cards tab root.
 */
class CardApplicationFragment : Fragment() {

    private val weAnalytics = WebEngage.get().analytics()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_card_application, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardId = arguments?.getString(ARG_CARD_ID)
        val card = CardRepository.findById(requireContext(), cardId)
            ?: CardRepository.getCards(requireContext()).firstOrNull()

        // Eyebrow shows the specific card name.
        card?.let { view.findViewById<TextView>(R.id.cardEyebrow).text = it.name }

        view.findViewById<View>(R.id.backButton).setOnClickListener {
            (parentFragment as? CardsFlowFragment)?.goBack()
        }

        val fullName = view.findViewById<EditText>(R.id.fullNameEditText)
        val agree = view.findViewById<CheckBox>(R.id.agreeCheckBox)
        val submit = view.findViewById<Button>(R.id.submitButton)
        val formView = view.findViewById<LinearLayout>(R.id.formView)
        val successView = view.findViewById<LinearLayout>(R.id.successView)

        // Prefill Full name with the session name (matches the website).
        fullName.setText(SessionManager.getUsername())

        // Submit enabled only while the mandatory agreement is checked.
        fun refreshSubmit() {
            submit.isEnabled = agree.isChecked
            submit.alpha = if (agree.isChecked) 1f else 0.5f
        }
        refreshSubmit()
        agree.setOnCheckedChangeListener { _, _ -> refreshSubmit() }

        submit.setOnClickListener {
            if (!agree.isChecked) return@setOnClickListener

            val attributes = mutableMapOf<String, Any>(
                "cardId" to (card?.id ?: cardId.orEmpty()),
                "tier" to SessionManager.getTier()
            )
            weAnalytics.track(Constants.EVENT_CARD_APPLICATION_COMPLETED, attributes)

            formView.visibility = View.GONE
            successView.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                if (isAdded) {
                    (activity as? MainActivity)?.resetCardsFlow()
                }
            }, 1500)
        }
    }

    override fun onStart() {
        super.onStart()
        // Tag the screen on every landing (fresh or via back), per WebEngage docs.
        weAnalytics.screenNavigated("Card Application")
    }

    companion object {
        const val ARG_CARD_ID = "card_id"
    }
}
