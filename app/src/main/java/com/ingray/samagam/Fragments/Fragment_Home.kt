package com.ingray.samagam.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ingray.samagam.Adapters.ClubsAdapter
import com.ingray.samagam.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val itemList: MutableList<Any> = mutableListOf()

    private lateinit var recyclerView: RecyclerView
    private lateinit var gridAdapter: ClubsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment__home, container, false)
        recyclerView = view.findViewById(R.id.clubs_Recycler)
        gridAdapter = ClubsAdapter(itemList)
        recyclerView.adapter = gridAdapter
        addItemsToRecyclerView(10)


        return view

    }

    private fun addItemsToRecyclerView(count: Int) {
        for (i in 1..count) {
            itemList.add("Item $i") // Assuming your itemList is a list of strings
        }

        // Notify the adapter about the new items
        gridAdapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment_Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}