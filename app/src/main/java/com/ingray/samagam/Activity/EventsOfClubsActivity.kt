package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.Adapters.ClubsAdapter
import com.ingray.samagam.Adapters.ClubsEventAdapter
import com.ingray.samagam.DataClass.Clubs
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.R
import java.util.*

class EventsOfClubsActivity : AppCompatActivity() {
    private lateinit var club_event_recycler:RecyclerView
    private lateinit var clubsEventAdapter: ClubsEventAdapter
    private lateinit var clubName:TextView
    private lateinit var nothing: ImageView
    private  var arrList:ArrayList<String> = ArrayList<String>()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_of_clubs)
        val itnt = intent
        var name = itnt.getStringExtra("clubName")!!

        club_event_recycler= findViewById(R.id.clubs_event_Recycler)
        clubName = findViewById(R.id.clubName)
       nothing = findViewById(R.id.nothing)
        clubName.text = name
        club_event_recycler.itemAnimator = null

        FirebaseDatabase.getInstance().reference.child("Clubs").child(name).child("Events")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            if (snap.child("purl").exists()) {
                                if(snap.child("purl").value.toString().isNotEmpty()) {
                                    arrList.add(snap.child("purl").value.toString())
                                }
                            }
                        }
                        if (arrList.size>0) {
                            try {
                                val random = Random()
                                val randomIndex1 = random.nextInt(arrList.size)
                                val randomIndex2 = random.nextInt(arrList.size)
                                val map = HashMap<String, String>()
                                map.put("eventpic1", arrList[randomIndex1])
                                map.put("eventpic2", arrList[randomIndex2])
                                FirebaseDatabase.getInstance().reference.child("Clubs").child(name)
                                    .updateChildren(map as Map<String, Any>)
                            }catch (e:Exception){}

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        FirebaseDatabase.getInstance().reference.child("Clubs").child(name).child("Events").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = snapshot.childrenCount
                if(count>0){
                    nothing.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        FirebaseDatabase.getInstance().reference.child("Clubs").child(name).child("Events").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(s in snapshot.children){
                        val map = HashMap<String,String>()
                        val str:String = s.key.toString()
                        map.put("key",str)
                        s.ref.updateChildren(map as Map<String, Any>)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        val options: FirebaseRecyclerOptions<Events?> =
            FirebaseRecyclerOptions.Builder<Events>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("Clubs").child(name).child("Events").orderByChild("event_date_time"),
                    Events::class.java
                )
                .build()
        clubsEventAdapter = ClubsEventAdapter(this,options,name)

        club_event_recycler.adapter = clubsEventAdapter
        clubsEventAdapter.startListening()
    }

}