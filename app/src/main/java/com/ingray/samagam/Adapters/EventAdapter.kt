package com.ingray.samagam.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.ingray.samagam.CustomDialog

class EventAdapter(options: FirebaseRecyclerOptions<Events?>) :
    FirebaseRecyclerAdapter<Events?, EventAdapter.userAdapterHolder?>(options) {
    override fun onBindViewHolder(
        holder: userAdapterHolder,
        position: Int,
        model: Events
    ) {
        holder.event_name.setText(model.event_name)
        Glide.with(holder.posterImage.context).load(model.purl).into(holder.posterImage)
        holder.date.setText("Today")
        holder.time.setText(model.event_endtime)
        holder.venue.setText(model.event_venue)
        holder.club_name.setText(model.club_name)

        holder.posterImage.setOnClickListener{
            val customDialog = CustomDialog(holder.itemView.context, model)
            customDialog.showDialog()
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): userAdapterHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.live_item,parent,false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(innerView:View):RecyclerView.ViewHolder(innerView) {
        var posterImage:ImageView
        var event_name:TextView
        var club_name:TextView
        var date:TextView
        var time:TextView
        var venue:TextView

        init {
            posterImage =innerView.findViewById(R.id.postImage)
            event_name = innerView.findViewById(R.id.event_name)
            club_name = innerView.findViewById(R.id.club_name)
            date = innerView.findViewById(R.id.date)
            time = innerView.findViewById(R.id.time)
            venue = itemView.findViewById(R.id.venue)
        }
    }
}