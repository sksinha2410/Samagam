package com.ingray.samagam.Adapters

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.Activity.LoginActivity
import com.ingray.samagam.DataClass.Notification
import com.ingray.samagam.DataClass.Posts
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationAdapter (options: FirebaseRecyclerOptions<Notification?>) :
    FirebaseRecyclerAdapter<Notification?, NotificationAdapter .userAdapterHolder?>(options) {

    override fun onBindViewHolder(
        holder: userAdapterHolder,
        position: Int,
        model: Notification
    ) {
        val previousTimeMillis = model.time.toLong()
        val currentTimeMillis = Calendar.getInstance().timeInMillis
        val timeDifferenceMillis = currentTimeMillis - previousTimeMillis
        val hoursDifference = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis)/10
        Glide.with(holder.image.context).load(model.imageUrl).into(holder.image)
        Glide.with(holder.notifImage.context).load(model.clubUrl).into(holder.notifImage)
        holder.title.setText(model.title)
        holder.message.setText(model.message)

        var map = HashMap<String,Any>()
        map.put("timeDifference",hoursDifference)
        FirebaseDatabase.getInstance().reference.child("Notification").child(model.time).updateChildren(map).addOnCompleteListener{
            if (it.isSuccessful){
                val deleteTime = 6*24*15
                if (model.timeDifference>deleteTime){
                    FirebaseDatabase.getInstance().reference.child("Notification").child(model.time).removeValue()
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): userAdapterHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.notif_item,parent,false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(innerView:View):RecyclerView.ViewHolder(innerView) {
        var title: TextView = innerView.findViewById(R.id.title)
        var message: TextView = innerView.findViewById(R.id.message)
        var image: ImageView = innerView.findViewById(R.id.notifImage)
        var notifImage: CircleImageView = innerView.findViewById(R.id.image)
    }
}