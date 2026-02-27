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
import com.webengage.demo.shopping.MainActivity
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.utils.Gender
import com.webengage.demo.shopping.view.InlineFragment

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {

    val weUser = WebEngage.get().user()
    val weAnalytics = WebEngage.get().analytics()
    private var mSharedPrefsManager: SharedPrefsManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        val storedUSerName = mSharedPrefsManager!!.getString(SharedPrefsManager.USERNAME, "")
        val data = mutableMapOf<String, Any>()

        if (TextUtils.isEmpty(storedUSerName)) {
            data["isUserLoggedIn"] = false
        } else {
            data["isUserLoggedIn"] = true;
            data["userName"] = storedUSerName;
        }
        
        WebEngage.get().analytics().screenNavigated("Login Screen")
    }

    private fun updateUserAttributes() {
        val latitude = 19.0822
        val longitude = 72.8417
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mSharedPrefsManager = SharedPrefsManager.get()
        val storedUSerName = mSharedPrefsManager!!.getString(SharedPrefsManager.USERNAME, "")

        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val usernameEditText = view.findViewById<TextInputEditText>(R.id.usernameEditText)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val inlineButton = view.findViewById<Button>(R.id.inlineButton)
        
        if (TextUtils.isEmpty(storedUSerName)) {
            showLoginElements()
            usernameTextView.visibility = View.GONE
        } else {
            welcomeUser(storedUSerName, usernameTextView)
            hideLoginElements()
        }
        
        inlineButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InlineFragment())
                .addToBackStack(null)
                .commit()
        }
        loginButton.setOnClickListener {
            val userName = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (isValidCredentials(userName, password)) {
                hideLoginElements()
                welcomeUser(userName, usernameTextView)
                updateUserAttributes()
                mSharedPrefsManager!!.put(SharedPrefsManager.USERNAME, userName)
                
                weUser.login(userName)
                
                (activity as? MainActivity)?.let { mainActivity ->
                    if (android.os.Build.VERSION.SDK_INT >= 33) {
                        mainActivity.checkForPushPermission()
                    }
                    mainActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, com.webengage.demo.shopping.view.home.HomeFragment())
                        .commit()
                }
            } else {
                Toast.makeText(
                    activity?.applicationContext,
                    "Please check the user name entered",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        logoutButton.setOnClickListener {
            mSharedPrefsManager!!.put(SharedPrefsManager.USERNAME, "")
            weUser.logout()
            showLoginElements()
            usernameTextView.visibility = View.GONE
        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storedUSerName = mSharedPrefsManager!!.getString(SharedPrefsManager.USERNAME, "")
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