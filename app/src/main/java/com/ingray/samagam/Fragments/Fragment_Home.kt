package com.ingray.samagam.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.ingray.samagam.Adapters.ClubsAdapter
import com.ingray.samagam.DataClass.Clubs
import com.ingray.samagam.R

class Fragment_Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clubsAdapter: ClubsAdapter
    private lateinit var progress: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment__home, container, false)
        recyclerView = view.findViewById(R.id.clubs_Recycler)
        progress =view.findViewById(R.id.sale_progressBar)
        recyclerView.itemAnimator = null

        val options: FirebaseRecyclerOptions<Clubs?> =
            FirebaseRecyclerOptions.Builder<Clubs>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("Clubs"),
                    Clubs::class.java
                )
                .build()
        clubsAdapter = ClubsAdapter(options)
        recyclerView.adapter = clubsAdapter
        clubsAdapter.startListening()

        progress.visibility = View.INVISIBLE
        return view
    }
}