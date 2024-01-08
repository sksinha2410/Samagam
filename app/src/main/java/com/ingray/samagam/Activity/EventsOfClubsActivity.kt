package com.ingray.samagam.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ingray.samagam.R

class EventsOfClubsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_of_clubs)
        val xmlButton = findViewById<Button>(R.id.xmlButton)
        val itnt = intent
        var name = itnt.getStringExtra("eventName")
        xmlButton.setOnClickListener {
            val xmlLink = "https://www.youtube.com/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(xmlLink))
            startActivity(intent)
        }
    }
}