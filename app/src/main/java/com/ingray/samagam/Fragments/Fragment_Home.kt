package com.ingray.samagam.Fragments

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.ingray.samagam.Adapters.ClubsAdapter
import com.ingray.samagam.Adapters.ClubsShimmerAdapter
import com.ingray.samagam.DataClass.Clubs
import com.ingray.samagam.R

class Fragment_Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val handler = android.os.Handler(Looper.getMainLooper())
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var clubsAdapter: ClubsAdapter
    private lateinit var clubsShimmerAdapter: ClubsShimmerAdapter
    private lateinit var progress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment__home, container, false)

        recyclerView = view.findViewById(R.id.clubs_Recycler)
        progress = view.findViewById(R.id.sale_progressBar)
        shimmerFrameLayout = view.findViewById(R.id.shimmerRelativeLayout)

        // Start the shimmer effect and show the progress bar
        shimmerFrameLayout.startShimmer()
        progress.visibility = View.GONE

        // Setup the placeholder adapter for shimmer effect
        val itemList = MutableList(20) { "Item ${it + 1}" }
        clubsShimmerAdapter = ClubsShimmerAdapter(itemList)
        recyclerView.adapter = clubsShimmerAdapter

        // Set up clubsAdapter for Firebase data
        val options: FirebaseRecyclerOptions<Clubs?> =
            FirebaseRecyclerOptions.Builder<Clubs>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("Clubs"),
                    Clubs::class.java
                )
                .build()
        clubsAdapter = ClubsAdapter(options)
        clubsAdapter.startListening()

        // Hide shimmer, progress, and show real data when adapter data is loaded
        clubsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()

                Log.d("Fragment_Home", "Data loaded in adapter")

                // Stop shimmer effect
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE

                // Hide progress bar
                progress.visibility = View.GONE
                // Show real data
                recyclerView.visibility = View.VISIBLE

                Log.d("Fragment_Home", "ProgressBar hidden")
            }
        })

        // Set the real adapter for recyclerView after shimmer delay
        handler.postDelayed({
            recyclerView.adapter = clubsAdapter
        }, 3000)

        return view
    }

    override fun onDestroyView() {
        shimmerFrameLayout.stopShimmer()
        progress.visibility = View.GONE
        super.onDestroyView()
    }
}
