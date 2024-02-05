package com.ingray.samagam.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ingray.samagam.R

class ClubsShimmerAdapter(private val itemList: List<String>) : RecyclerView.Adapter<ClubsShimmerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.club_item_shimmer, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Bind your data to views here
    }

    override fun getItemCount(): Int = itemList.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}