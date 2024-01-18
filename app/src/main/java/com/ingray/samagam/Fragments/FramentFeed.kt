package com.ingray.samagam.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
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

class FramentFeed : Fragment() {
    lateinit var feedRecycler:RecyclerView
    lateinit var feedAdapter: FeedAdapter
    private var dataBaseRef= FirebaseDatabase.getInstance().reference
    lateinit var searchEditText:EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_frament_feed, container, false)
        feedRecycler = view.findViewById(R.id.feed_Recycler)
        searchEditText = view.findViewById(R.id.etSearch)
        feedRecycler.itemAnimator = null

        val options: FirebaseRecyclerOptions<Posts?> = FirebaseRecyclerOptions.Builder<Posts>().
        setQuery(dataBaseRef.child("Posts").orderByChild("hrsAgo"), Posts::class.java).build()
        feedAdapter = FeedAdapter(options)
        feedRecycler.adapter = feedAdapter
        feedAdapter.startListening()



        try {
            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                    // Nothing to do here
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    val searchText =
                        charSequence.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }

                    // Update your adapter with the new options
                    try {
                        // Create a query based on the entered text
                        val query = FirebaseDatabase.getInstance().reference.child("Posts")
                            .orderByChild("event_name")
                            .startAt(searchText)
                            .endAt(searchText + "\uf8ff")
                        val options: FirebaseRecyclerOptions<Posts?> =
                            FirebaseRecyclerOptions.Builder<Posts>()
                                .setQuery(query, Posts::class.java)
                                .build()
                        feedAdapter.updateOptions(options)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Orientation changed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun afterTextChanged(editable: Editable) {
                    // Nothing to do here
                }
            })
        } catch (e: Exception) {
        }

        return view
    }
}