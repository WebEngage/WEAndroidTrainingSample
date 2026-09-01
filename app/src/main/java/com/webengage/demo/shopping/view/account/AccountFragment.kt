package com.webengage.demo.shopping.view.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
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
        view.findViewById<TextView>(R.id.accountUsername).text = SessionManager.getUsername()
        view.findViewById<TextView>(R.id.accountTier).text = SessionManager.getTier()

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
}
