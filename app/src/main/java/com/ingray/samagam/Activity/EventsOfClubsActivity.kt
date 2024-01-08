package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.ingray.samagam.Adapters.ClubsAdapter
import com.ingray.samagam.Adapters.ClubsEventAdapter
import com.ingray.samagam.DataClass.Clubs
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.R

class EventsOfClubsActivity : AppCompatActivity() {
    private lateinit var club_event_recycler:RecyclerView
    private lateinit var clubsEventAdapter: ClubsEventAdapter
    private lateinit var clubName:TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_of_clubs)
        val itnt = intent
        var name = itnt.getStringExtra("clubName")!!

        club_event_recycler= findViewById(R.id.clubs_event_Recycler)
        clubName = findViewById(R.id.clubName)
        clubName.text = name
        club_event_recycler.itemAnimator = null

        val options: FirebaseRecyclerOptions<Events?> =
            FirebaseRecyclerOptions.Builder<Events>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("Clubs").child(name).child("Events"),
                    Events::class.java
                )
                .build()
        clubsEventAdapter = ClubsEventAdapter(options)
        club_event_recycler.adapter = clubsEventAdapter
        clubsEventAdapter.startListening()



    }
}