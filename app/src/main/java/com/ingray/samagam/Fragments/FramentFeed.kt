package com.ingray.samagam.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.ingray.samagam.Adapters.EventAdapter
import com.ingray.samagam.Adapters.FeedAdapter
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.DataClass.Posts
import com.ingray.samagam.R

class FramentFeed : Fragment() {
    lateinit var feedRecycler:RecyclerView
    lateinit var feedAdapter: FeedAdapter
    private var dataBaseRef= FirebaseDatabase.getInstance().reference

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_frament_feed, container, false)
        feedRecycler = view.findViewById(R.id.feed_Recycler)
        feedRecycler.itemAnimator = null


        val options: FirebaseRecyclerOptions<Posts?> = FirebaseRecyclerOptions.Builder<Posts>().
        setQuery(dataBaseRef.child("Feed"), Posts::class.java).build()
        feedAdapter = FeedAdapter(options)
        feedRecycler.adapter = feedAdapter
        feedAdapter.startListening()

        return view
    }

    
}