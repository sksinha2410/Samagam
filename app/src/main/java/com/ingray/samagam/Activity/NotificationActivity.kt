package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.ingray.samagam.Adapters.NotificationAdapter
import com.ingray.samagam.Adapters.ReportedPostAdapter
import com.ingray.samagam.Constants.Companion.SERVER_KEY
import com.ingray.samagam.DataClass.Notification
import com.ingray.samagam.R
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class NotificationActivity : AppCompatActivity() {
    private lateinit var notificationRecycler: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationRecycler = findViewById(R.id.notificationRecycler)
        notificationRecycler.setHasFixedSize(true)
        notificationRecycler.itemAnimator = null
        
        val options: FirebaseRecyclerOptions<Notification?> =
            FirebaseRecyclerOptions.Builder<Notification>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("Notification").orderByChild("timeDifference"),
                    Notification::class.java
                )
                .build()
        notificationAdapter = NotificationAdapter(options)
        notificationRecycler.adapter = notificationAdapter
        notificationAdapter.startListening()
    }
}
