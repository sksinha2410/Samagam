package com.ingray.samagam.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.Adapters.AlumniBatchAdapter
import com.ingray.samagam.Adapters.ClubMembersAdapter
import com.ingray.samagam.DataClass.Alumni
import com.ingray.samagam.DataClass.MembersProfile
import com.ingray.samagam.R
import java.util.*
import kotlin.collections.ArrayList

class  ClubMembersDetailActivity : AppCompatActivity() {
    private lateinit var batchName: TextView
    private lateinit var members: RecyclerView
    private lateinit var addAlumni: FloatingActionButton
    private lateinit var adapt:ClubMembersAdapter
    private  var arrList:ArrayList<String> = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_members_detail)
        val intent = intent
        val name =intent.getStringExtra("clubName")
        val batch = intent.getStringExtra("batch")
        callbyId()
        batchName.text = batch
        members.itemAnimator=null

        FirebaseDatabase.getInstance().reference.child("Clubs").child(name!!)
            .child("Alumni").child(batch!!).child("Members").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        for (snap in snapshot.children) {
                            if (snap.child("purl").exists()) {
                                arrList.add(snap.child("purl").value.toString())
                            }
                        }
                        val random = Random()
                        val randomIndex1 = random.nextInt(arrList.size)
                        val randomIndex2 = random.nextInt(arrList.size)
                        val randomIndex3 = random.nextInt(arrList.size)
                        val randomIndex4 = random.nextInt(arrList.size)
                        val map = HashMap<String,String>()
                        map.put("profile1",arrList[randomIndex1])
                        map.put("profile2",arrList[randomIndex2])
                        map.put("profile3",arrList[randomIndex3])
                        map.put("profile4",arrList[randomIndex4])
                        FirebaseDatabase.getInstance().reference.child("Clubs").child(name)
                            .child("Alumni").child(batch).updateChildren(map as Map<String, Any>)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        val options: FirebaseRecyclerOptions<Alumni?> =
            FirebaseRecyclerOptions.Builder<Alumni>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("Clubs").child(name)
                        .child("Alumni").child(batch).child("Members"),
                    Alumni::class.java
                )
                .build()
        adapt = ClubMembersAdapter(options)
        members.adapter = adapt
        adapt.startListening()


        addAlumni.setOnClickListener{
            val intent = Intent(applicationContext,AddAlumniActivity::class.java)
            intent.putExtra("Clubname",name)
            intent.putExtra("batch",batch)
            startActivity(intent)

        }

    }

    private fun callbyId() {
        batchName=findViewById(R.id.batchName)
        members=findViewById(R.id.members_recycler)
        addAlumni = findViewById(R.id.addAlumni)
    }
}