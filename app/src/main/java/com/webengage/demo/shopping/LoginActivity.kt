package com.webengage.demo.shopping

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.webengage.sdk.android.WebEngage

/**
 * Login screen — no bottom-nav chrome (it's a standalone Activity).
 * Local/foreground mock only: username + relationship tier are stored in the
 * in-app session and pushed to WebEngage as the CUID + custom attribute.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameLayout = findViewById<TextInputLayout>(R.id.usernameLayout)
        val usernameEditText = findViewById<TextInputEditText>(R.id.usernameEditText)
        val tierSpinner = findViewById<Spinner>(R.id.tierSpinner)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Exactly 4 tier options: tier_1..tier_4
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Constants.TIER_OPTIONS
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tierSpinner.adapter = adapter

        loginButton.setOnClickListener {
            val username = usernameEditText.text?.toString()?.trim().orEmpty()
            if (username.isEmpty()) {
                usernameLayout.error = "Username is required"
                return@setOnClickListener
            }
            usernameLayout.error = null

            val selectedTier = tierSpinner.selectedItem as String

            // Store the session (in-memory/SharedPreferences session object)
            SessionManager.saveSession(username, selectedTier)

            // WebEngage: login + set the relationship_value_tier custom attribute
            WebEngage.get().user().login(username)
            WebEngage.get().user().setAttribute(
                Constants.ATTR_RELATIONSHIP_VALUE_TIER,
                selectedTier
            )

            Toast.makeText(this, "Welcome, $username", Toast.LENGTH_SHORT).show()

            // Navigate to the Home shell, clearing Login from the back stack
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
