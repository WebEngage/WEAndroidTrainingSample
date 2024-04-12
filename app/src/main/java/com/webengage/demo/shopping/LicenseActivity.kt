package com.webengage.demo.shopping

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.webengage.personalization.WEPersonalization.Companion.get
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks
import com.webengage.sdk.android.WebEngageConfig

class LicenseActivity : AppCompatActivity() {

    private var mLicense = ""
    private var jsonUrl = ""
    private var mLicenseEditText: AutoCompleteTextView? = null
    private var mUrlEditText: AutoCompleteTextView? = null
    private var mEngageButton: Button? = null
    private var mSharedPrefsManager: SharedPrefsManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
        initData()
        initViews()
    }

    private fun initData() {
        mSharedPrefsManager = SharedPrefsManager.get()
        if (mSharedPrefsManager!!.getBoolean(Constants.WEBENGAGE_ENGAGED, false)) {
            goToHome()
            finish()
        } else {
            mLicense = mSharedPrefsManager!!.getString(
                Constants.LICENSE_CODE,
                ""
            )
        }
    }

    private fun initViews() {
        mLicenseEditText = findViewById(R.id.licenseEditText)!!
        mUrlEditText = findViewById(R.id.urlEditText)
        mLicenseEditText?.setText(mLicense)


        mEngageButton = findViewById(R.id.saveButton)!!
        mEngageButton?.setOnClickListener {
            mEngageButton?.isEnabled = false
            mLicense = mLicenseEditText!!.text.toString().trim { it <= ' ' }
            jsonUrl = mUrlEditText!!.text.toString().trim { it <= ' ' }
            if(!Utils.isBlank(jsonUrl)) {
                mSharedPrefsManager!!.put(Constants.JSON_URL,jsonUrl)
            }
            if (mLicense.isNotEmpty()) {
                saveLicenseCode(mLicense)
                get().init()
                engage()
                Handler().postDelayed({
                    goToHome()
                    mEngageButton!!.isEnabled = true
                    finish()
                }, 1000)
            } else {
                Utils.showToast("Invalid license code")
                mEngageButton!!.isEnabled = false
            }
        }
    }

    private fun saveLicenseCode(license: String) {
        mSharedPrefsManager!!.put(Constants.LICENSE_CODE, license)
    }

    private fun engage() {
        val webEngageConfig: WebEngageConfig = WebEngageManager.buildWebEngageConfig(mLicense)
        (ShoppingApplication.getAppContext() as ShoppingApplication).registerActivityLifecycleCallbacks(
            WebEngageActivityLifeCycleCallbacks(this, webEngageConfig)
        )
        WebEngage.engage(this, webEngageConfig)
        mSharedPrefsManager!!.put(Constants.WEBENGAGE_ENGAGED, true)
        WebEngageManager.registerCallbacks()
    }

    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}