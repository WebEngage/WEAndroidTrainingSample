package com.webengage.demo.shopping.view.recharge

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.R
import com.webengage.sdk.android.WebEngage

class RechargeFragment : Fragment() {

    private val weAnalytics = WebEngage.get().analytics()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recharge, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        view.findViewById<ImageView>(R.id.searchIcon).setOnClickListener { Log.d("RechargeFragment", "Search Clicked") }
        view.findViewById<ImageView>(R.id.logoutIcon).setOnClickListener { 
            Log.d("RechargeFragment", "Logout Clicked")
            com.webengage.demo.shopping.SharedPrefsManager.get().put(com.webengage.demo.shopping.SharedPrefsManager.USERNAME, "")
            WebEngage.get().user().logout()
            (activity as? com.webengage.demo.shopping.MainActivity)?.let { mainActivity ->
                mainActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(com.webengage.demo.shopping.R.id.bottom_navigation)?.visibility = View.GONE
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(com.webengage.demo.shopping.R.id.fragment_container, com.webengage.demo.shopping.view.user.UserFragment())
                    .commit()
            }
        }

        val mobileNumberInput = view.findViewById<EditText>(R.id.mobileNumberInput)
        val amountInput = view.findViewById<EditText>(R.id.amountInput)

        view.findViewById<Button>(R.id.btnContinue).setOnClickListener {
            val mobileNumber = mobileNumberInput.text.toString()
            val amount = amountInput.text.toString()

            if (mobileNumber.isEmpty() || amount.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            weAnalytics.track("recharge_initiated")

            val progressDialog = ProgressDialog(requireContext()).apply {
                setMessage("Processing recharge...")
                setCancelable(false)
                show()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
                
                val map: MutableMap<String, Any> = HashMap()
                map["recharge_number"] = mobileNumber.toLongOrNull() ?: 0L
                map["recharge_amount"] = amount.toDoubleOrNull() ?: 0.0
                weAnalytics.track("recharge_completed", map)

                AlertDialog.Builder(requireContext())
                    .setTitle("Success")
                    .setMessage("Recharge successful!")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
                    
                mobileNumberInput.setText("")
                amountInput.setText("")
            }, 1000)
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            mobileNumberInput.setText("")
            amountInput.setText("")
            Log.d("RechargeFragment", "Cancel Pressed")
        }
    }
}
