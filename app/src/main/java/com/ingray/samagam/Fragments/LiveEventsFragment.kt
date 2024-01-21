package com.ingray.samagam.Fragments

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
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
import com.ingray.samagam.Adapters.EventAdapterPast
import com.ingray.samagam.Adapters.EventAdapterUpcoming
import com.ingray.samagam.Constants
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.R
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
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
    private lateinit var eventAdapterUpcoming: EventAdapterUpcoming
    private lateinit var eventAdapterPast: EventAdapterPast
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
        dRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val ev = childSnapshot.getValue(Events::class.java)
                    if (ev != null) {



                        val isChecked:Boolean = checkTimeandDate(ev.event_date,ev.event_starttime,ev.event_endtime)
                        if(isChecked){
                            dataBaseRef.child("Live").child(ev.key).setValue(ev).addOnCompleteListener{
                                if (ev.notified == "1" ||ev.notified=="0") {
                                    sendNotification(
                                        ev.event_name,
                                        ev.purl,
                                        ev.club_name,
                                        "Event is Live"
                                    )
                                    val map = HashMap<String,String>()
                                    map.put("notified","2")
                                    dataBaseRef.child("Events").child(ev.key).updateChildren(
                                        map as Map<String, Any>
                                    )
                                }
                            }


                        }
                        val isBefore:Boolean = isBeforeGivenDateTime(ev.event_date,ev.event_starttime)
                        if(isBefore){
                            val dateCalendar:Calendar=Calendar.getInstance()
                            dateCalendar.time= inputDateFormat.parse(ev.event_date)!!
                            val isToday = isCurrentDate(dateCalendar)

                            if(isToday){

                                val map = HashMap<String,String>()
                                if(ev.notified == "0") {
                                    map.put("notified", "1")
                                    dataBaseRef.child("Events").child(ev.key).updateChildren(
                                        map as Map<String, Any>
                                    )
                                    sendNotification(
                                        ev.event_name,
                                        ev.purl,
                                        ev.club_name,
                                        "Event Scheduled: Today at ${ev.event_starttime}"
                                    )
                                }

                            }
                            dataBaseRef.child("Upcoming").child(ev.key).setValue(ev)

                        }
                        val isAfter:Boolean = isAfterGivenDateTime(ev.event_date,ev.event_endtime)
                        if(isAfter){
                            dataBaseRef.child("PastEvents").child(ev.key).setValue(ev)
                        }
                    }
                }
                val options:FirebaseRecyclerOptions<Events?> = FirebaseRecyclerOptions.Builder<Events>().
                setQuery(dataBaseRef.child("Live").orderByChild("event_date_time"), Events::class.java).build()
                eventAdapter = EventAdapter(options)
                liveEventRecycler.adapter = eventAdapter
                eventAdapter.startListening()

                val options2:FirebaseRecyclerOptions<Events?> = FirebaseRecyclerOptions.Builder<Events>().
                setQuery(dataBaseRef.child("Upcoming").orderByChild("event_date_time"), Events::class.java).build()
                eventAdapterUpcoming = EventAdapterUpcoming(options2)
                upcomingEventRecycler.adapter = eventAdapterUpcoming
                eventAdapterUpcoming.startListening()

                val options3:FirebaseRecyclerOptions<Events?> = FirebaseRecyclerOptions.Builder<Events>().
                setQuery(dataBaseRef.child("PastEvents").orderByChild("hrsAgo"), Events::class.java).build()
                eventAdapterPast = EventAdapterPast(options3)
                pastEventRecycler.adapter = eventAdapterPast
                eventAdapterPast.startListening()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
                Log.e("Firebase", "Error fetching events: ${error.message}")
            }
        })

        val database = FirebaseDatabase.getInstance()
        val upcomingRef = database.getReference("Upcoming")
        val liveRef = database.getReference("Live")

// Get current date and time
        val currentDate:String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime:String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

// Retrieve events from Firebase
        upcomingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (eventSnapshot in dataSnapshot.children) {
                    val event = eventSnapshot.getValue(Events::class.java)

                    if (event != null &&
                        event.event_date < currentDate
                    ) {
                        // Delete events that have passed the start time with date
                        eventSnapshot.ref.removeValue()

                    }

                    else if (event != null && event.event_date == currentDate && event.event_starttime<=currentTime){
                        eventSnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle potential errors here
            }
        })
        liveRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (eventSnapshot in dataSnapshot.children) {
                    val event = eventSnapshot.getValue(Events::class.java)

                    if (event != null &&
                        event.event_date < currentDate
                    ) {
                        // Delete events that have passed the start time with date
                        eventSnapshot.ref.removeValue()

                    }

                    else if (event != null && event.event_date == currentDate && event.event_endtime<currentTime){
                        eventSnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle potential errors here
            }
        })
        return view
    }

    private fun sendNotification(name:String,purl: String,clubName:String,message: String) {
        val title = "$clubName:$name"
        val message = message

        // Check if title and message are not empty
        if (title.isNotEmpty() && message.isNotEmpty()) {
            // AsyncTask to send the notification in the background
            sendNotificationWithImage(title, message, purl)
        } else {
            // Display an error message if title or message is empty
        }
    }

    private fun sendNotificationWithImage(title: String, message: String, imageUrl: String) {
        val title = title
        val message = message
        val imageUrl = imageUrl

        // AsyncTask to send the notification in the background
        SendNotificationTask().execute(title, message, imageUrl)
    }

    private inner class SendNotificationTask : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg params: String): Boolean {
            val title = params[0]
            val message = params[1]
            val imageUrl = params[2]

            return try {
                // FCM server URL
                val url = URL("https://fcm.googleapis.com/fcm/send")

                // Create a connection
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Authorization", "key=${Constants.SERVER_KEY}")

                // Enable input/output streams
                conn.doOutput = true

                // Create JSON payload
                val jsonPayload = JSONObject().apply {
                    put("to", "/topics/All")
                    val data = JSONObject().apply {
                        put("title", title)
                        put("message", message)
                        put("imageUrl", imageUrl)
                    }
                    put("data", data)
                }

                // Log statements for debugging
                Log.d("SendNotificationTask", "Notification payload: $jsonPayload")

                // Write JSON payload to the connection's output stream
                val os = conn.outputStream
                os.write(jsonPayload.toString().toByteArray(Charsets.UTF_8))
                os.close()

                // Get the response code
                val responseCode: Int = conn.responseCode

                // Log statements for debugging
                Log.d("SendNotificationTask", "Response code: $responseCode")

                // Close the connection
                conn.disconnect()

                // Check if the notification was sent successfully (HTTP status code 200)
                responseCode == 200
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SendNotificationTask", "Error sending notification: ${e.message}")
                false
            }
        }

        override fun onPostExecute(result: Boolean) {

        }
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

            // Check if the current date and time are after the given date and time
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