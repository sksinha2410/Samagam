package com.ingray.samagam


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.DataClass.Notification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifCustomDialog(private val context: Context, private val model: Notification) {

    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.notif_dialog, null)

        // Access dialog views and set model data
        val title = dialogView.findViewById<TextView>(R.id.title)
        val message = dialogView.findViewById<TextView>(R.id.message)
        val time = dialogView.findViewById<TextView>(R.id.time)
        val imageUrl = dialogView.findViewById<ImageView>(R.id.imageUrl)
        val description = dialogView.findViewById<TextView>(R.id.description)
        val pdfLink = dialogView.findViewById<TextView>(R.id.pdfLink)

        // Set model data to the views
        title.text = model.title
        message.text = model.message
        val dateunf = model.time.toLong()
        val dateInDate = Date(dateunf)
        val date = inputDateFormat.format(dateInDate)
        pdfLink.text = model.pdfLink
        description.text = model.description
        val dateup = date.substring(8,10)+date.substring(4,8)+date.substring(0,4)
        time.text = dateup

        Glide.with(dialogView.context).load(model.imageUrl).into(imageUrl)

        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}