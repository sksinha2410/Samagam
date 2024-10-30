package com.ingray.samagam.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.Adapters.FeedAdapter
import com.ingray.samagam.DataClass.Posts
import com.ingray.samagam.R
import java.util.Locale
import com.facebook.shimmer.ShimmerFrameLayout


class FramentFeed : Fragment() {
    private lateinit var feedRecycler: RecyclerView
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var feedAdapter: FeedAdapter
    private val dataBaseRef = FirebaseDatabase.getInstance().reference
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_frament_feed, container, false)

        feedRecycler = view.findViewById(R.id.feed_Recycler)
        shimmerLayout = view.findViewById(R.id.shimmerRelativeLayout)
        searchEditText = view.findViewById(R.id.etSearch)

        // Enable smooth scrolling
        feedRecycler.setHasFixedSize(true)
        feedRecycler.isNestedScrollingEnabled = false

        // Start shimmer animation
        shimmerLayout.startShimmer()

        val options: FirebaseRecyclerOptions<Posts?> = FirebaseRecyclerOptions.Builder<Posts>()
            .setQuery(dataBaseRef.child("Posts").orderByChild("hrsAgo"), Posts::class.java).build()

        feedAdapter = FeedAdapter(options)
        feedRecycler.adapter = feedAdapter

        feedAdapter.startListening()
        loadFeedData()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val searchText = charSequence.toString().lowercase(Locale.getDefault()).trim()
                val query = dataBaseRef.child("Posts")
                    .orderByChild("event_name")
                    .startAt(searchText)
                    .endAt(searchText + "\uf8ff")

                val newOptions = FirebaseRecyclerOptions.Builder<Posts>()
                    .setQuery(query, Posts::class.java)
                    .build()

                feedAdapter.updateOptions(newOptions)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        return view
    }

    private fun loadFeedData() {
        dataBaseRef.child("Posts").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Stop shimmer animation and show the RecyclerView
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                feedRecycler.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors here
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        feedAdapter.stopListening()
    }
}
