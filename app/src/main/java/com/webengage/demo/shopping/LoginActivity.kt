package com.webengage.demo.shopping

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.webengage.sdk.android.WebEngage

/**
 * Login screen — no bottom-nav chrome (standalone Activity), matching the website's
 * /login page: "Welcome back", a Full name field, a tier dropdown with a
 * "Select a tier…" placeholder + labeled options, a "Log in" button and a footer note.
 *
 * Local/foreground mock only: name + selected tier are stored in the session and
 * pushed to WebEngage as the CUID + relationship_value_tier custom attribute.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val tierSpinner = findViewById<Spinner>(R.id.tierSpinner)
        val tierError = findViewById<TextView>(R.id.tierError)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Dropdown: index 0 is the "Select a tier…" placeholder, then the 4 tiers
        // using the website's "Tier N — …" labels.
        val labels = listOf(getString(R.string.login_tier_placeholder)) +
            resources.getStringArray(R.array.tier_labels).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tierSpinner.adapter = adapter

        loginButton.setOnClickListener {
            // Empty name defaults to "Priya Sharma" (matches the website).
            val name = nameEditText.text?.toString()?.trim().orEmpty().ifBlank { "Priya Sharma" }

            val tierIndex = tierSpinner.selectedItemPosition
            if (tierIndex <= 0) {
                tierError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            tierError.visibility = View.GONE
            val selectedTier = Constants.TIER_OPTIONS[tierIndex - 1]

            // Store the session (in-memory/SharedPreferences session object).
            SessionManager.saveSession(name, selectedTier)

            // WebEngage: login + set the relationship_value_tier custom attribute.
            WebEngage.get().user().login(name)
            WebEngage.get().user().setFirstName(name)
            WebEngage.get().user().setAttribute(
                Constants.ATTR_RELATIONSHIP_VALUE_TIER,
                selectedTier
            )

            // Navigate to the Home shell, clearing Login from the back stack.
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
