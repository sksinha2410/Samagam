package com.ingray.samagam.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.Adapters.EventAdapter
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
    private lateinit var liveEventRecycler:RecyclerView
    private lateinit var upcomingEventRecycler:RecyclerView
    private lateinit var pastEventRecycler:RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventAdapterUpcoming: EventAdapter
    private lateinit var eventAdapterPast: EventAdapter
    private var dataBaseRef= FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view:View= inflater.inflate(R.layout.fragment_live_events, container, false)
        contex=view.context
        callId(view)
        liveEventRecycler.itemAnimator = null
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val ev = childSnapshot.getValue(Events::class.java)
                    if (ev != null) {
                        val isChecked:Boolean = checkTimeandDate(ev.event_date,ev.event_starttime,ev.event_endtime)
                        if(isChecked){
                            dataBaseRef.child("Live").child(ev.event_name).setValue(ev)
                        }
                        val isBefore:Boolean = isBeforeGivenDateTime(ev.event_date,ev.event_starttime)
                        if(isBefore){
                            dataBaseRef.child("Upcoming").child(ev.event_name).setValue(ev)
                        }
                        val isAfter:Boolean = isAfterGivenDateTime(ev.event_date,ev.event_endtime)
                        if(isAfter){
                            dataBaseRef.child("PastEvents").child(ev.event_name).setValue(ev)
                        }
                    }
                }
                val options:FirebaseRecyclerOptions<Events?> = FirebaseRecyclerOptions.Builder<Events>().
                setQuery(dataBaseRef.child("Live"), Events::class.java).build()
                eventAdapter = EventAdapter(options)
                liveEventRecycler.adapter = eventAdapter
                eventAdapter.startListening()

                val options2:FirebaseRecyclerOptions<Events?> = FirebaseRecyclerOptions.Builder<Events>().
                setQuery(dataBaseRef.child("Upcoming"), Events::class.java).build()
                eventAdapterUpcoming = EventAdapter(options2)
                upcomingEventRecycler.adapter = eventAdapterUpcoming
                eventAdapterUpcoming.startListening()

                val options3:FirebaseRecyclerOptions<Events?> = FirebaseRecyclerOptions.Builder<Events>().
                setQuery(dataBaseRef.child("PastEvents"), Events::class.java).build()
                eventAdapterPast = EventAdapter(options3)
                pastEventRecycler.adapter = eventAdapterPast
                eventAdapterPast.startListening()
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
                return true
                }
        }
        return false

    }

    private fun isCurrentDate(dateCalendar: Calendar) : Boolean {
      val currentdate=Calendar.getInstance()

      return (dateCalendar.get(Calendar.YEAR)==currentdate.get(Calendar.YEAR)&&dateCalendar.get(Calendar.MONTH)==currentdate.get(Calendar.MONTH)
                &&dateCalendar.get(Calendar.DATE)==currentdate.get(Calendar.DATE))
    }

    fun isBeforeGivenDateTime(givenDate: String, givenTime: String): Boolean {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Get current date and time
        val currentDateTime = Calendar.getInstance().time

        try {
            // Combine the given date and time strings into one date-time string
            val givenDateTimeStr = "$givenDate $givenTime"

            // Parse the given date-time string into a Date object
            val givenDateTime = inputDateFormat.parse(givenDateTimeStr)

            // Check if the current date and time are before the given date and time
            return currentDateTime.before(givenDateTime)
        } catch (e: Exception) {
            // Handle parsing exceptions

        }

        return false
    }
    fun isAfterGivenDateTime(givenDate: String, givenTime: String): Boolean {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Get current date and time
        val currentDateTime = Calendar.getInstance().time

        try {
            // Combine the given date and time strings into one date-time string
            val givenDateTimeStr = "$givenDate $givenTime"

            // Parse the given date-time string into a Date object
            val givenDateTime = inputDateFormat.parse(givenDateTimeStr)

            // Check if the current date and time are before the given date and time
            return currentDateTime.after(givenDateTime)
        } catch (e: Exception) {
            // Handle parsing exceptions

        }

        return false
    }



    private fun callId(view: View) {
        database=FirebaseDatabase.getInstance()
        dRef=database.getReference("Events")
        liveEventRecycler= view.findViewById(R.id.liveEvent_Recycler)
        upcomingEventRecycler= view.findViewById(R.id.upcoming_Recycler)
        pastEventRecycler= view.findViewById(R.id.past_Recycler)
    }
}