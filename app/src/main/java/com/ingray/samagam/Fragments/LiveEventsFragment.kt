package com.ingray.samagam.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LiveEventsFragment : Fragment() {
    private lateinit var database:FirebaseDatabase
    private lateinit var dRef: DatabaseReference
    val inputDateFormat= SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val inputTimeFormat= SimpleDateFormat("HH:mm", Locale.getDefault())
    lateinit var contex: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View= inflater.inflate(R.layout.fragment_live_events, container, false)
        contex=view.context
        callId()
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val ev = childSnapshot.getValue(Events::class.java)
                    if (ev != null) {
                        checkTimeandDate(ev.event_date,ev.event_starttime,ev.event_endtime)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
                Log.e("Firebase", "Error fetching events: ${error.message}")
            }
        })
        return view
    }

    private fun checkTimeandDate(
        eventDate: String,
        eventStarttime: String,
        eventEndtime: String
    ): Boolean {
        val dateCalendar:Calendar=Calendar.getInstance()
        dateCalendar.time= inputDateFormat.parse(eventDate)!!
        val check:Boolean= isCurrentDate(dateCalendar)
        if(check){
            val currenttime=inputTimeFormat.parse(inputTimeFormat.format(Date()))
            val starttime=inputTimeFormat.parse(eventStarttime)
            val endtime=inputTimeFormat.parse(eventEndtime)
            val isBetweentime=currenttime.after(starttime)&&currenttime.before(endtime)
            if(isBetweentime){
                Toast.makeText(contex,"check passed",Toast.LENGTH_LONG).show()
                }
        }


        return false

    }

    private fun isCurrentDate(dateCalendar: Calendar) : Boolean {
      val currentdate=Calendar.getInstance()
        return (dateCalendar.get(Calendar.YEAR)==currentdate.get(Calendar.YEAR)&&dateCalendar.get(Calendar.MONTH)==currentdate.get(Calendar.MONTH)
                &&dateCalendar.get(Calendar.DATE)==currentdate.get(Calendar.DATE))
    }


    private fun callId() {
        database=FirebaseDatabase.getInstance()
        dRef=database.getReference("Events")

    }


}