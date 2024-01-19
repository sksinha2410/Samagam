package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.ingray.samagam.R

class ClubDetailsActivity : AppCompatActivity() {
    private lateinit var cvEvents:CardView
    private lateinit var clubName: TextView
    private lateinit var clubNames: TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_details)

        val itnt = intent
        var name = itnt.getStringExtra("clubName")!!
        var names = "$name Events"

        cvEvents = findViewById(R.id.clubEvent)
        clubName = findViewById(R.id.clubName)
        clubNames = findViewById(R.id.clubNames)

        clubName.text = names
        clubNames.text = name

        cvEvents.setOnClickListener{
            val intent= Intent(this,EventsOfClubsActivity::class.java)
            intent.putExtra("clubName",name)
            startActivity(intent)
        }


    }
}