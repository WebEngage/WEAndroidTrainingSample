package com.webengage.demo.shopping.view.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.Constants
import com.webengage.demo.shopping.LoginActivity
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SessionManager
import com.webengage.sdk.android.WebEngage

/**
 * Account — read-only view of the session username and selected tier.
 * Logout clears the local session, calls WebEngage.get().user().logout(), and
 * returns to Login clearing the back stack.
 */
class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = SessionManager.getUsername()
        val tier = SessionManager.getTier()

        view.findViewById<TextView>(R.id.avatar).text = initials(name)
        view.findViewById<TextView>(R.id.accountName).text = name
        // Tier pill maps the value to its label (e.g. "tier_2 — Gold").
        view.findViewById<TextView>(R.id.accountTierPill).text = Constants.tierLabel(tier)
        // Email derived from the CUID (first name), e.g. "priya@email.com".
        view.findViewById<TextView>(R.id.accountEmail).text = emailFromName(name)

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            SessionManager.clear()
            WebEngage.get().user().logout()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Tag the screen on every landing (fresh or via back), per WebEngage docs.
        WebEngage.get().analytics().screenNavigated("Account")
    }

    /**
     * Derives a demo email from the CUID (the user's first name), e.g.
     * "Priya Sharma" -> "priya@email.com". Non-alphanumeric chars are stripped.
     */
    private fun emailFromName(name: String): String {
        val firstName = name.trim().substringBefore(" ")
            .lowercase()
            .filter { it.isLetterOrDigit() }
        val handle = firstName.ifBlank { "user" }
        return "$handle@email.com"
    }

    /** First + last initial (matches the website's avatar). */
    private fun initials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val first = parts.firstOrNull()?.firstOrNull()?.toString() ?: ""
        val last = if (parts.size > 1) parts.last().first().toString() else ""
        return (first + last).uppercase().ifBlank { "U" }
    }
}
