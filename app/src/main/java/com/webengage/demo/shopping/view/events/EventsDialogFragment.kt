package com.webengage.demo.shopping.view.events

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.webengage.demo.shopping.R
import com.webengage.sdk.android.WebEngage

class EventsDialogFragment : DialogFragment() {

    private val weAnalytics = WebEngage.get().analytics()
    private val eventAttributes = mutableMapOf<String, Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_events, container, false)
        initViews(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun initViews(view: View) {
        val eventNameInput = view.findViewById<EditText>(R.id.eventNameInput)
        val attributeKeyInput = view.findViewById<EditText>(R.id.attributeKeyInput)
        val attributeValueInput = view.findViewById<EditText>(R.id.attributeValueInput)
        val attributeTypeSpinner = view.findViewById<Spinner>(R.id.attributeTypeSpinner)
        val addAttributeButton = view.findViewById<Button>(R.id.addAttributeButton)
        val attributesListView = view.findViewById<TextView>(R.id.attributesListView)
        val trackEventButton = view.findViewById<Button>(R.id.trackEventButton)
        val closeButton = view.findViewById<ImageView>(R.id.closeButton)

        val types = arrayOf("String", "Number", "Boolean")
        attributeTypeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, types)

        addAttributeButton.setOnClickListener {
            val key = attributeKeyInput.text.toString()
            val value = attributeValueInput.text.toString()
            val type = attributeTypeSpinner.selectedItem.toString()

            if (key.isNotEmpty() && value.isNotEmpty()) {
                when (type) {
                    "String" -> eventAttributes[key] = value
                    "Number" -> eventAttributes[key] = value.toDoubleOrNull() ?: value
                    "Boolean" -> eventAttributes[key] = value.toBoolean()
                }
                updateAttributesList(attributesListView)
                attributeKeyInput.setText("")
                attributeValueInput.setText("")
            }
        }

        trackEventButton.setOnClickListener {
            val eventName = eventNameInput.text.toString()
            if (eventName.isNotEmpty()) {
                if (eventAttributes.isEmpty()) {
                    weAnalytics.track(eventName)
                } else {
                    weAnalytics.track(eventName, eventAttributes)
                }
                Log.d("EventsDialog", "Event tracked: $eventName with attributes: $eventAttributes")
                Toast.makeText(context, "Event tracked: $eventName", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(context, "Please enter event name", Toast.LENGTH_SHORT).show()
            }
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun updateAttributesList(textView: TextView) {
        val list = eventAttributes.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        textView.text = if (list.isEmpty()) "No attributes added" else list
    }
}
