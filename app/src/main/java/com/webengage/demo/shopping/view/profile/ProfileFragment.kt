package com.webengage.demo.shopping.view.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SharedPrefsManager
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.utils.Gender
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private val weUser = WebEngage.get().user()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        view.findViewById<ImageView>(R.id.backIcon).setOnClickListener { 
            parentFragmentManager.popBackStack()
        }

        val firstNameInput = view.findViewById<EditText>(R.id.firstNameInput)
        val lastNameInput = view.findViewById<EditText>(R.id.lastNameInput)
        val emailInput = view.findViewById<EditText>(R.id.emailInput)
        val phoneInput = view.findViewById<EditText>(R.id.phoneInput)
        val companyInput = view.findViewById<EditText>(R.id.companyInput)
        val birthDateInput = view.findViewById<EditText>(R.id.birthDateInput)
        val genderSpinner = view.findViewById<Spinner>(R.id.genderSpinner)
        val customKeyInput = view.findViewById<EditText>(R.id.customKeyInput)
        val customValueInput = view.findViewById<EditText>(R.id.customValueInput)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        val genderOptions = arrayOf("Select", "Male", "Female", "Other")
        genderSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, genderOptions)

        saveButton.setOnClickListener {
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val email = emailInput.text.toString()
            val phone = phoneInput.text.toString()
            val company = companyInput.text.toString()
            val birthDate = birthDateInput.text.toString()
            val gender = genderSpinner.selectedItem.toString()
            val customKey = customKeyInput.text.toString()
            val customValue = customValueInput.text.toString()

            if (firstName.isNotEmpty()) weUser.setFirstName(firstName)
            if (lastName.isNotEmpty()) weUser.setLastName(lastName)
            if (email.isNotEmpty()) weUser.setEmail(email)
            if (phone.isNotEmpty()) weUser.setPhoneNumber(phone)
            if (company.isNotEmpty()) weUser.setCompany(company)
            
            if (birthDate.isNotEmpty()) {
                weUser.setBirthDate(birthDate)
            }

            when (gender) {
                "Male" -> weUser.setGender(Gender.MALE)
                "Female" -> weUser.setGender(Gender.FEMALE)
                "Other" -> weUser.setGender(Gender.OTHER)
            }

            if (customKey.isNotEmpty() && customValue.isNotEmpty()) {
                weUser.setAttribute(customKey, customValue)
            }

            Toast.makeText(context, "Profile saved successfully", Toast.LENGTH_SHORT).show()
            Log.d("ProfileFragment", "Profile saved")
        }
    }
}
