package com.webengage.demo.shopping.view.user

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SharedPrefsManager


/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        val storedUSerName = SharedPrefsManager.getString(SharedPrefsManager.USERNAME, "")
        val data = mutableMapOf<String, Any>()

        if (TextUtils.isEmpty(storedUSerName)) {
            data["isUserLoggedIn"] = false
        } else {
            data["isUserLoggedIn"] = true;
            data["userName"] = storedUSerName;
        }
    }

    private fun updateUserAttributes() {
        val latitude = 19.0822
        val longitude = 72.8417
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        SharedPrefsManager.get(activity?.applicationContext!!)
        val storedUSerName = SharedPrefsManager.getString(SharedPrefsManager.USERNAME, "")

        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val usernameEditText = view.findViewById<TextInputEditText>(R.id.usernameEditText)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        if (TextUtils.isEmpty(storedUSerName)) {
            showLoginElements()
            usernameTextView.visibility = View.GONE
        } else {
            welcomeUser(storedUSerName, usernameTextView)
            hideLoginElements()
        }
        loginButton.setOnClickListener {
            val userName = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            // Perform login/authentication logic here
            if (isValidCredentials(userName, password)) {
                hideLoginElements()
                welcomeUser(userName, usernameTextView)
                updateUserAttributes()
                SharedPrefsManager.putString(SharedPrefsManager.USERNAME, userName)
            } else {
                // Display an error message or handle failed login
                //show error
                Toast.makeText(
                    activity?.applicationContext,
                    "Please check the user name entered",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        logoutButton.setOnClickListener {
            val userName = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            // Perform login/authentication logic here
            SharedPrefsManager.putString(SharedPrefsManager.USERNAME, "")
            showLoginElements()
        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storedUSerName = SharedPrefsManager.getString(SharedPrefsManager.USERNAME, "")
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        if (TextUtils.isEmpty(storedUSerName)) {
            showLoginElements()
            usernameTextView.visibility = View.GONE
        } else {
            usernameTextView.visibility = View.VISIBLE
            usernameTextView.setText(
                "Welcome ".plus(storedUSerName).plus(" to the Shopping festival..!!")
            )
            hideLoginElements()
        }
    }

    private fun isValidCredentials(username: String, password: String): Boolean {
        // Implement your authentication logic here
        // For simplicity, this example does basic validation
        return !TextUtils.isEmpty(username)
    }

    private fun welcomeUser(userName: String, usernameTextView: TextView) {
        usernameTextView.visibility = View.VISIBLE
        usernameTextView.text = ("Welcome\n".plus(userName).plus("\nto the Shopping festival"))
    }

    private fun showLoginElements() {
        val usernameEditText = view?.findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = view?.findViewById<EditText>(R.id.passwordEditText)
        usernameEditText?.visibility = View.VISIBLE
        usernameEditText?.setText("")
        passwordEditText?.setText("")
        passwordEditText?.visibility = View.VISIBLE
        view?.findViewById<LinearLayoutCompat>(R.id.ll_logIn)?.visibility = View.VISIBLE
        view?.findViewById<LinearLayoutCompat>(R.id.ll_logout)?.visibility = View.GONE
    }

    private fun hideLoginElements() {
        val usernameEditText = view?.findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = view?.findViewById<EditText>(R.id.passwordEditText)
        usernameEditText?.visibility = View.GONE
        passwordEditText?.visibility = View.GONE
        view?.findViewById<LinearLayoutCompat>(R.id.ll_logIn)?.visibility = View.GONE
        view?.findViewById<LinearLayoutCompat>(R.id.ll_logout)?.visibility = View.VISIBLE
    }
}