package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.ingray.samagam.Adapters.ClubsAdapter
import com.ingray.samagam.Adapters.ClubsEventAdapter
import com.ingray.samagam.Adapters.ReportedPostAdapter
import com.ingray.samagam.DataClass.Clubs
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.DataClass.Posts
import com.ingray.samagam.R

class ReportedPost : AppCompatActivity() {
    private lateinit var rep_recycler:RecyclerView
    private lateinit var reportedPostAdapter: ReportedPostAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reported_post)

        rep_recycler= findViewById(R.id.rep_Recycler)

        rep_recycler.itemAnimator = null

        val options: FirebaseRecyclerOptions<Posts?> =
            FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("ReportedPost"),
                    Posts::class.java
                )
                .build()
        reportedPostAdapter = ReportedPostAdapter(options)
        rep_recycler.adapter = reportedPostAdapter
        reportedPostAdapter.startListening()
    }
}