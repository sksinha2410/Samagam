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

class CustomDialog(private val context: Context, private val model: Events) {

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.activity_event_description, null)

        // Access dialog views and set model data
        val eventName = dialogView.findViewById<TextView>(R.id.event_name)
        val eventType = dialogView.findViewById<TextView>(R.id.event_type)
        val time = dialogView.findViewById<TextView>(R.id.time)
        val poster = dialogView.findViewById<ImageView>(R.id.poster)
        val description = dialogView.findViewById<TextView>(R.id.description)
        val regLink = dialogView.findViewById<TextView>(R.id.reg_link)
        val brochureLink = dialogView.findViewById<TextView>(R.id.brochure_link)

        // Set model data to the views
        eventName.text = model.event_name
        eventType.text = model.event_type
        val date = model.event_date
        val times = model.event_starttime
        val timee = model.event_endtime
        val dateup = date.substring(8,10)+date.substring(4,8)+date.substring(0,4)
        time.text = "$dateup ($times - $timee)"
        description.text = model.description
        regLink.text = model.reg_link
        brochureLink.text = model.brochure_link
        Glide.with(dialogView.context).load(model.purl).into(poster)

        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}