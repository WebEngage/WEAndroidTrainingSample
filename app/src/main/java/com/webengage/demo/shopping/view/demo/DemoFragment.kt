package com.webengage.demo.shopping.view.demo

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.SharedPrefsManager
import com.webengage.sdk.android.WebEngage

class DemoFragment : Fragment() {

    val weAnalytics = WebEngage.get().analytics()

    var clientName = ""
    lateinit var clientInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_demo, container, false)
        initViews(view)
        return view
    }

    private fun trackEvent(eventName: String, view: View) {
        val map: MutableMap<String, Any> = HashMap()
        val mClient: String = clientInput.text.toString().trim()
        if (!clientName.equals(mClient, false) && !TextUtils.isEmpty(mClient)) {
            clientName = mClient;
            SharedPrefsManager.get().put("clientName", mClient)
        }
        map["clientName"] = clientName
        weAnalytics.track(eventName, map)
    }

    private fun initViews(view: View) {
        clientInput = view.findViewById<EditText>(R.id.clientName)
        clientName = SharedPrefsManager.get().getString("clientName", "")

        clientInput.setText(clientName)

        val mTimerButton = view.findViewById<Button>(R.id.push_timer).also {
            it.setOnClickListener({
                trackEvent("Timer", view)
            })
        }

        val mCarouselButton = view.findViewById<Button>(R.id.push_carousel).also {
            it.setOnClickListener({
                trackEvent("Carousel", view)
            })
        }

        val mBannerButton = view.findViewById<Button>(R.id.push_banner).also {
            it.setOnClickListener({
                trackEvent("Banner", view)
            })
        }

        val mInappSpinTheWheel = view.findViewById<Button>(R.id.inapp_stw).also {
            it.setOnClickListener({
                trackEvent("SpinTheWheel", view)
            })
        }

        val mInappScratchCard = view.findViewById<Button>(R.id.inapp_scratch).also {
            it.setOnClickListener({
                trackEvent("ScratchCard", view)
            })
        }

        val mInappClassic = view.findViewById<Button>(R.id.inapp_cm).also {
            it.setOnClickListener({
                trackEvent("ClassicModal", view)
            })
        }

        val mInappNPS = view.findViewById<Button>(R.id.inapp_nps).also {
            it.setOnClickListener({
                trackEvent("NPS", view)
            })
        }

    }
}