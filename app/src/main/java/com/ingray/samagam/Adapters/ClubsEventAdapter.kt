package com.ingray.samagam.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.ingray.samagam.CustomDialog

class ClubsEventAdapter(options: FirebaseRecyclerOptions<Events?>) :
    FirebaseRecyclerAdapter<Events?, ClubsEventAdapter.userAdapterHolder?>(options) {
    override fun onBindViewHolder(
        holder: userAdapterHolder,
        position: Int,
        model: Events
    ) {
        holder.progress.visibility = View.VISIBLE
        holder.event_name.setText(model.event_name)
        holder.date.setText(model.event_date)
        holder.time.setText(model.event_starttime)
        holder.venue.setText(model.event_venue)
        Glide.with(holder.posterImage.context).load(model.purl).into(holder.posterImage)

        holder.posterImage.setOnClickListener{
            val customDialog = CustomDialog(holder.itemView.context, model)
            customDialog.showDialog()
        }

        holder.reg_link.setOnClickListener {
            val xmlLink = model.reg_link
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(xmlLink))
                holder.itemView.context.startActivity(intent)
            }catch (e:Exception){
                Toast.makeText(holder.itemView.context,"No Registration link Available",Toast.LENGTH_SHORT).show()
            }


        }
        holder.brochure_link.setOnClickListener {
            val xmlLink = model.brochure_link
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(xmlLink))
                holder.itemView.context.startActivity(intent)
            }catch (e:Exception){
                Toast.makeText(holder.itemView.context,"No Brochure link Available",Toast.LENGTH_SHORT).show()
            }


        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): userAdapterHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.club_event_item,parent,false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(innerView:View):RecyclerView.ViewHolder(innerView) {
        var posterImage:ImageView
        var event_name:TextView
        var brochure_link:CardView
        var reg_link:CardView
        var date:TextView
        var time:TextView
        var venue:TextView
        var progress:ProgressBar

        init {
            posterImage =innerView.findViewById(R.id.event_poster)
            event_name = innerView.findViewById(R.id.event_name)
            brochure_link = innerView.findViewById(R.id.rulebook)
            reg_link = innerView.findViewById(R.id.register)
            date = innerView.findViewById(R.id.date)
            time = innerView.findViewById(R.id.time)
            venue = innerView.findViewById(R.id.venue)
            progress=innerView.findViewById(R.id.sale_progressBar)
        }
    }
}