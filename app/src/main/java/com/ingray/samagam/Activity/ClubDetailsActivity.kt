package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var eventPic1:ImageView
    private lateinit var eventPic2:ImageView
    private lateinit var add:ImageView
    private var userType:String=""
    private var uid:String=FirebaseAuth.getInstance().currentUser?.uid.toString()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_details)
        cvEvents = findViewById(R.id.clubEvent)
        clubName = findViewById(R.id.clubName)
        clubNames = findViewById(R.id.clubNames)
        cvOffice = findViewById(R.id.cvOffice)
        profile1 = findViewById(R.id.profile1)
        profile2 = findViewById(R.id.profile2)
        profile3 = findViewById(R.id.profile3)
        profile4 = findViewById(R.id.profile4)
        eventPic1 = findViewById(R.id.eventpic1)
        eventPic2 = findViewById(R.id.eventpic2)
        add = findViewById(R.id.add)

        val itnt = intent
        var name = itnt.getStringExtra("clubName")!!
        try {
            var eventpic1 = itnt.getStringExtra("eventpic1")!!
            var eventpic2 = itnt.getStringExtra("eventpic2")!!

            Glide.with(applicationContext).load(eventpic1).into(eventPic1)
            Glide.with(applicationContext).load(eventpic2).into(eventPic2)
        }catch (e:Exception){}

        var names = "$name Events"


        FirebaseDatabase.getInstance().reference.child("Users").child(uid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("userType").exists()) {
                    userType = snapshot.child("userType").value.toString()
                    if(!userType.equals("0")){
                        add.visibility = View.VISIBLE
                    }else{
                        add.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
        add.setOnClickListener{
            var selectedItemAtPosition: String = "Events"

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setTitle("What do you want to add?")
                .setPositiveButton("OK") { dialog, which ->
                    // Show a Toast with the selected item when the positive button is clicked
                    if(selectedItemAtPosition == "Events"){
                        val intent = Intent(applicationContext,AddEventsActivity::class.java)
                        intent.putExtra("userType",userType)
                        startActivity(intent)
                    }else{
                        if (selectedItemAtPosition == "OfficeBearer") {
                            val intent = Intent(applicationContext, AddAlumniActivity::class.java)
                            intent.putExtra("Clubname",name)
                            intent.putExtra("batch","0")
                            intent.putExtra("type",selectedItemAtPosition)
                            startActivity(intent)
                        }else{
                            val intent = Intent(applicationContext, AddAlumniActivity::class.java)
                            intent.putExtra("Clubname",name)
                            intent.putExtra("batch","2021-2025")
                            intent.putExtra("type",selectedItemAtPosition)
                            startActivity(intent)

                        }
                    }

                    // Do something.
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setSingleChoiceItems(
                    R.array.item_array, 0
                ) { dialog, which ->
                    // Store the selected item
                    val items = resources.getStringArray(R.array.item_array)
                    selectedItemAtPosition = items[which]
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()



        }
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