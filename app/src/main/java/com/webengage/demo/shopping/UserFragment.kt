package com.webengage.demo.shopping

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        SharedPrefsManager.get(activity?.applicationContext!!)
        val storedUSerName = SharedPrefsManager.getString(SharedPrefsManager.USERNAME,"")

        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val usernameEditText = view.findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        if(TextUtils.isEmpty(storedUSerName)){
            showLoginElements()
            usernameTextView.visibility = View.GONE
        }else{
            welcomeUser(storedUSerName,usernameTextView)
            hideLoginElements()
        }
        loginButton.setOnClickListener {
            val userName = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            // Perform login/authentication logic here
            if (isValidCredentials(userName, password)) {
                hideLoginElements()
                welcomeUser(userName,usernameTextView)
                SharedPrefsManager.putString(SharedPrefsManager.USERNAME,userName)
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
            SharedPrefsManager.putString(SharedPrefsManager.USERNAME,"")
            showLoginElements()
        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storedUSerName = SharedPrefsManager.getString(SharedPrefsManager.USERNAME,"")
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        if(TextUtils.isEmpty(storedUSerName)){
            showLoginElements()
            usernameTextView.visibility = View.GONE
        }else{
            usernameTextView.visibility = View.VISIBLE
            usernameTextView.setText("Welcome ".plus(storedUSerName).plus(" to the Shopping festival..!!"))
            hideLoginElements()
        }
    }
    private fun isValidCredentials(username: String, password: String): Boolean {
        // Implement your authentication logic here
        // For simplicity, this example does basic validation
        return !TextUtils.isEmpty(username)
    }
    private fun welcomeUser(userName:String, usernameTextView:TextView){
        usernameTextView.visibility = View.VISIBLE
        usernameTextView.setText("Welcome ".plus(userName).plus(" User to the Shopping festival"))
    }
    private fun showLoginElements(){
        val usernameEditText = view?.findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = view?.findViewById<EditText>(R.id.passwordEditText)
        val loginButton = view?.findViewById<Button>(R.id.loginButton)
        val logoutButton = view?.findViewById<Button>(R.id.logoutButton)
        usernameEditText?.visibility = View.VISIBLE
        passwordEditText?.visibility = View.VISIBLE
        loginButton?.visibility = View.VISIBLE
        logoutButton?.visibility = View.GONE
    }

    private fun hideLoginElements(){
        val usernameEditText = view?.findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = view?.findViewById<EditText>(R.id.passwordEditText)
        val loginButton = view?.findViewById<Button>(R.id.loginButton)
        val logoutButton = view?.findViewById<Button>(R.id.logoutButton)
        usernameEditText?.visibility = View.GONE
        passwordEditText?.visibility = View.GONE
        loginButton?.visibility = View.INVISIBLE
        logoutButton?.visibility = View.VISIBLE


    }
companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        const val TAG = "USER"
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}