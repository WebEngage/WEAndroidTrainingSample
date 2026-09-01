package com.webengage.demo.shopping.view.cards

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.webengage.demo.shopping.Constants
import com.webengage.demo.shopping.MainActivity
import com.webengage.demo.shopping.R
import com.webengage.sdk.android.WebEngage

/**
 * Card Application form:
 * - Full Name, Phone and Email fields (all optional).
 * - A mandatory "agree to Terms" checkbox, checked (enabled) by default. When it is
 *   unchecked the Submit button is disabled.
 * On submit fires card_application_completed, shows a success state, then returns
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
        view.findViewById<View>(R.id.backButton).setOnClickListener {
            (parentFragment as? CardsFlowFragment)?.goBack()
        }

        val nameEditText = view.findViewById<TextInputEditText>(R.id.fullNameEditText)
        val phoneEditText = view.findViewById<TextInputEditText>(R.id.phoneEditText)
        val emailEditText = view.findViewById<TextInputEditText>(R.id.emailEditText)
        val agreeCheckBox = view.findViewById<MaterialCheckBox>(R.id.agreeCheckBox)
        val submitButton = view.findViewById<Button>(R.id.submitButton)
        val formView = view.findViewById<LinearLayout>(R.id.formView)
        val successView = view.findViewById<LinearLayout>(R.id.successView)

        // Submit is enabled only while the mandatory agreement is checked.
        submitButton.isEnabled = agreeCheckBox.isChecked
        submitButton.alpha = if (agreeCheckBox.isChecked) 1f else 0.5f
        agreeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            submitButton.isEnabled = isChecked
            submitButton.alpha = if (isChecked) 1f else 0.5f
        }

        submitButton.setOnClickListener {
            if (!agreeCheckBox.isChecked) return@setOnClickListener

            // Name, phone and email are optional — capture whatever was entered.
            val fullName = nameEditText.text?.toString()?.trim().orEmpty()
            val phone = phoneEditText.text?.toString()?.trim().orEmpty()
            val email = emailEditText.text?.toString()?.trim().orEmpty()

            val attributes = mutableMapOf<String, Any>("agreed_terms" to true)
            if (fullName.isNotEmpty()) attributes["full_name"] = fullName
            if (phone.isNotEmpty()) attributes["phone"] = phone
            if (email.isNotEmpty()) attributes["email"] = email

            weAnalytics.track(Constants.EVENT_CARD_APPLICATION_COMPLETED, attributes)

            // Show success state, then return to the Cards tab root.
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
}
