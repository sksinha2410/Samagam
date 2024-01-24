package com.ingray.samagam.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.Adapters.ClubMembersAdapter
import com.ingray.samagam.DataClass.Alumni
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
        val default = "0"
        val uID = FirebaseAuth.getInstance().currentUser?.uid

        val batch = intent.getStringExtra("batch")?:default
        val type = intent.getStringExtra("type")
        callbyId()
        if (uID != null) {
            FirebaseDatabase.getInstance().reference.child("Users").child(uID).addListenerForSingleValueEvent(
                object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            var check:String=snapshot.child("userType").value.toString()
                            if (check == name || check == "1"){
                                addAlumni.visibility=View.VISIBLE
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
            )
        }
        if(!batch.equals("0")) {
            batchName.text = batch
        }else{
            batchName.text = "Office Bearers"
        }
        members.itemAnimator=null

        try {
            if (!batch.equals("0")){
                FirebaseDatabase.getInstance().reference.child("Clubs").child(name!!)
                    .child(type!!).child(batch!!).child("Members")
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
                                    val random = Random()
                                    val randomIndex1 = random.nextInt(arrList.size)
                                    val randomIndex2 = random.nextInt(arrList.size)
                                    val randomIndex3 = random.nextInt(arrList.size)
                                    val randomIndex4 = random.nextInt(arrList.size)
                                    val map = HashMap<String, String>()
                                    map.put("profile1", arrList[randomIndex1])
                                    map.put("profile2", arrList[randomIndex2])
                                    map.put("profile3", arrList[randomIndex3])
                                    map.put("profile4", arrList[randomIndex4])
                                    FirebaseDatabase.getInstance().reference.child("Clubs")
                                        .child(name)
                                        .child(type).child(batch)
                                        .updateChildren(map as Map<String, Any>)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }else{
                FirebaseDatabase.getInstance().reference.child("Clubs").child(name!!)
                    .child(type!!).child("Members")
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
                                val random = Random()
                                val randomIndex1 = random.nextInt(arrList.size)
                                val randomIndex2 = random.nextInt(arrList.size)
                                val randomIndex3 = random.nextInt(arrList.size)
                                val randomIndex4 = random.nextInt(arrList.size)
                                val map = HashMap<String, String>()
                                map.put("profile1", arrList[randomIndex1])
                                map.put("profile2", arrList[randomIndex2])
                                map.put("profile3", arrList[randomIndex3])
                                map.put("profile4", arrList[randomIndex4])
                                FirebaseDatabase.getInstance().reference.child("Clubs").child(name)
                                    .child(type)
                                    .updateChildren(map as Map<String, Any>)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }
        }catch (e:Exception){

        }

        try {
            if(!batch.equals("0")) {

                val options: FirebaseRecyclerOptions<Alumni?> =
                    FirebaseRecyclerOptions.Builder<Alumni>()
                        .setQuery(
                            FirebaseDatabase.getInstance().reference.child("Clubs").child(name!!)
                                .child(type!!).child(batch!!).child("Members").orderByChild("weightage"),
                            Alumni::class.java
                        )
                        .build()
                adapt = ClubMembersAdapter(options,name)
                members.adapter = adapt
                adapt.startListening()
            }else{
                val options: FirebaseRecyclerOptions<Alumni?> =
                    FirebaseRecyclerOptions.Builder<Alumni>()
                        .setQuery(
                            FirebaseDatabase.getInstance().reference.child("Clubs").child(name!!)
                                .child(type!!).child("Members").orderByChild("weightage"),
                            Alumni::class.java
                        )
                        .build()
                adapt = ClubMembersAdapter(options,name)
                members.adapter = adapt
                adapt.startListening()
            }

        }catch (e:Exception){
            Toast.makeText(applicationContext,"Nothing there",Toast.LENGTH_SHORT).show()
        }




        addAlumni.setOnClickListener{
            val intent = Intent(applicationContext,AddAlumniActivity::class.java)
            intent.putExtra("Clubname",name)
            intent.putExtra("batch",batch)
            intent.putExtra("type",type)
            startActivity(intent)

        }

    }

    private fun callbyId() {
        batchName=findViewById(R.id.batchName)
        members=findViewById(R.id.members_recycler)
        addAlumni = findViewById(R.id.addAlumni)
    }
}