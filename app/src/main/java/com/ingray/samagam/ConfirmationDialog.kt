package com.ingray.samagam


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Button
import com.bumptech.glide.Glide
import com.ingray.samagam.DataClass.Events

class ConfirmationDialog(private val context: Context, private val model: Events) {

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.custom_dialog_confirmation, null)

        // Access dialog views and set model data
        val positive = dialogView.findViewById<Button>(R.id.positive)
        val negative = dialogView.findViewById<Button>(R.id.negative)
        positive.setOnClickListener{

        }

        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.setTitle("Hello")
        alertDialog.show()

    }
}