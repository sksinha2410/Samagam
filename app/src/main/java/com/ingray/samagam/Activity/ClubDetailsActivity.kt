package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.Adapters.AlumniBatchAdapter
import com.ingray.samagam.Adapters.ClubsEventAdapter
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.DataClass.MembersProfile
import com.ingray.samagam.R

class ClubDetailsActivity : AppCompatActivity() {
    private lateinit var cvEvents:CardView
    private lateinit var clubName: TextView
    private lateinit var clubNames: TextView
    private lateinit var alumni_batch_recycler: RecyclerView
    private lateinit var adapt: AlumniBatchAdapter

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
        alumni_batch_recycler = findViewById(R.id.alumni_batch_recycler)
        alumni_batch_recycler.itemAnimator = null

        clubName.text = names
        clubNames.text = name

        cvEvents.setOnClickListener{
            val intent= Intent(this,EventsOfClubsActivity::class.java)
            intent.putExtra("clubName",name)
            startActivity(intent)
        }

        val options: FirebaseRecyclerOptions<MembersProfile?> =
            FirebaseRecyclerOptions.Builder<MembersProfile>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("Clubs").child("Tesla").child("Alumni"),
                    MembersProfile::class.java
                )
                .build()
        adapt = AlumniBatchAdapter(options,name)

        alumni_batch_recycler.adapter = adapt
       adapt.startListening()
    }
}