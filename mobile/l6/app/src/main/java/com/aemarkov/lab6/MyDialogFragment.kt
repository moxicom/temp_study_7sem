package com.aemarkov.lab6

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class MyDialogFragment : DialogFragment() {

    private lateinit var listener: MyDialogFragmentListener

    interface MyDialogFragmentListener {
        fun onDialogSendClicked(text: String)
        fun onDialogExitClicked()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as MyDialogFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context должен реализовывать MyDialogFragmentListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_layout, null)

        val editTextInput = view.findViewById<EditText>(R.id.editTextInput)
        val buttonExit = view.findViewById<Button>(R.id.buttonExit)
        val buttonClose = view.findViewById<Button>(R.id.buttonClose)
        val buttonSend = view.findViewById<Button>(R.id.buttonSend)

        val dialog = builder.setView(view).create()

        buttonExit.setOnClickListener {
            listener.onDialogExitClicked()
            dialog.dismiss()
        }

        buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        buttonSend.setOnClickListener {
            val text = editTextInput.text.toString()
            if (text.isNotEmpty()) {
                listener.onDialogSendClicked(text)
                dialog.dismiss()
            }
        }

        return dialog
    }
}