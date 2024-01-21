package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
    private lateinit var cvOffice:CardView
    private lateinit var profile1:ImageView
    private lateinit var profile2:ImageView
    private lateinit var profile3:ImageView
    private lateinit var profile4:ImageView
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
        cvOffice = findViewById(R.id.cvOffice)
        profile1 = findViewById(R.id.profile1)
        profile2 = findViewById(R.id.profile2)
        profile3 = findViewById(R.id.profile3)
        profile4 = findViewById(R.id.profile4)
        alumni_batch_recycler = findViewById(R.id.alumni_batch_recycler)
        alumni_batch_recycler.itemAnimator = null

        clubName.text = names
        clubNames.text = name

        FirebaseDatabase.getInstance().reference.child("Clubs").child(name).child("OfficeBearer").addListenerForSingleValueEvent(
            object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val d = snapshot.getValue(MembersProfile::class.java)

                        try {
                            Glide.with(applicationContext).load(d?.profile1).into(profile1)
                            Glide.with(applicationContext).load(d?.profile2).into(profile2)
                            Glide.with(applicationContext).load(d?.profile3).into(profile3)
                            Glide.with(applicationContext).load(d?.profile4).into(profile4)
                        }catch (e:Exception){

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
        )
        cvOffice.setOnClickListener{
            val intent= Intent(this,ClubMembersDetailActivity::class.java)
            intent.putExtra("clubName",name)
            intent.putExtra("type","OfficeBearer")
            intent.putExtra("batch","0")
            startActivity(intent)
        }

        cvEvents.setOnClickListener{
            val intent= Intent(this,EventsOfClubsActivity::class.java)
            intent.putExtra("clubName",name)
            startActivity(intent)
        }

        val options: FirebaseRecyclerOptions<MembersProfile?> =
            FirebaseRecyclerOptions.Builder<MembersProfile>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("Clubs").child(name).child("Alumni"),
                    MembersProfile::class.java
                )
                .build()
        adapt = AlumniBatchAdapter(options,name)

        alumni_batch_recycler.adapter = adapt
       adapt.startListening()
    }
}