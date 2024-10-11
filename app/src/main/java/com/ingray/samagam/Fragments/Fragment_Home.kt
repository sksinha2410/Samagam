package com.ingray.samagam.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.ingray.samagam.Adapters.ClubsAdapter
import com.ingray.samagam.Adapters.ClubsShimmerAdapter
import com.ingray.samagam.AutoScrollManager
import com.ingray.samagam.DataClass.Clubs
import com.ingray.samagam.R

class Fragment_Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val handler = android.os.Handler(Looper.getMainLooper())
    private lateinit var shimmerRecyclerView: RecyclerView
    private lateinit var clubsAdapter: ClubsAdapter
    private lateinit var shimmerRelativeLayout: RelativeLayout
    private lateinit var clubsShimmerAdapter: ClubsShimmerAdapter
    private lateinit var progress: ProgressBar
    private lateinit var autoScrollManager: AutoScrollManager

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment__home, container, false)
        recyclerView = view.findViewById(R.id.clubs_Recycler)
        shimmerRecyclerView = view.findViewById(R.id.clubs_Recycler_Shimmer)
        progress = view.findViewById(R.id.sale_progressBar)
        shimmerRelativeLayout = view.findViewById(R.id.shimmerRelativeLayout)
        recyclerView.itemAnimator = null
        shimmerRelativeLayout.visibility = View.VISIBLE

        val itemList = mutableListOf<String>()
        // Add your data to the itemList (e.g., 20 items)
        for (i in 1..20) {
            itemList.add("Item $i")
        }

        clubsShimmerAdapter = ClubsShimmerAdapter(itemList)
        recyclerView.adapter = clubsShimmerAdapter


        handler.postDelayed({
            // Hide the shimmer and show the real data
            shimmerRelativeLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }, 30000)


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


        clubsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()

                // Now you can reliably get the total item count
                val itemCount = clubsAdapter.itemCount
                println("ItemCount: Total items = $itemCount")

                // Display the item count in a Toast
                Toast.makeText(view.context.applicationContext, itemCount.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        // Initialize AutoScrollManager and start auto-scrolling
//        autoScrollManager = AutoScrollManager(recyclerView)
//        autoScrollManager.startAutoScroll()

        progress.visibility = View.INVISIBLE
        return view
    }

//    override fun onDestroyView() {
//        // Stop auto-scrolling when the fragment is destroyed
//        autoScrollManager.stopAutoScroll()
//        super.onDestroyView()
//    }
}
