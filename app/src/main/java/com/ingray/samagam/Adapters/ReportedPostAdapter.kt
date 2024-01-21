package com.ingray.samagam.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.ingray.samagam.Activity.ReportedPost
import com.ingray.samagam.DataClass.Posts
import com.ingray.samagam.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar
import java.util.concurrent.TimeUnit


class ReportedPostAdapter(options: FirebaseRecyclerOptions<Posts?>) :
    FirebaseRecyclerAdapter<Posts?, ReportedPostAdapter.userAdapterHolder?>(options) {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val repRef =FirebaseDatabase.getInstance().reference.child("ReportedPost")
    private val feedRef =FirebaseDatabase.getInstance().reference.child("Posts")
    private val userRef =FirebaseDatabase.getInstance().reference.child("Users")
    override fun onBindViewHolder(
        holder: userAdapterHolder,
        position: Int,
        model: Posts
    ) {

        holder.username.setText(model.username)
        val previousTimeMillis = model.time.toLong()

        val currentTimeMillis = Calendar.getInstance().timeInMillis

        val timeDifferenceMillis = currentTimeMillis - previousTimeMillis

        val hoursDifference = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)

        if (hoursDifference/24 >= 1){
            holder.time.text = (hoursDifference/24).toString() + " days ago"
        }
        else{
            holder.time.text = hoursDifference.toString() + " hours ago"
        }

        Glide.with(holder.postImage.context).load(model.postUrl).into(holder.postImage)
        Glide.with(holder.profileImage.context).load(model.purl).into(holder.profileImage)


        holder.allow.setOnClickListener{
            repRef.child(model.postId).addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        snapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        holder.delete.setOnClickListener{
            repRef.child(model.postId).addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        snapshot.ref.removeValue()

                        Toast.makeText(holder.itemView.context,"Posts allowed",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            feedRef.child(model.postId).addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        snapshot.ref.removeValue()
                        Toast.makeText(holder.itemView.context,"Posts Deleted from feed",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            userRef.child(model.userId).child("Posts").child(model.postId).removeValue()
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): userAdapterHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.reported_item,parent,false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(innerView:View):RecyclerView.ViewHolder(innerView) {
        var profileImage: CircleImageView
        var postImage:ImageView
        var username:TextView
        var time:TextView
        var allow:TextView
        var delete:TextView

        init {
            profileImage = innerView.findViewById(R.id.profileImage)
            postImage = innerView.findViewById(R.id.postImage)
            username = innerView.findViewById(R.id.username)
            time = innerView.findViewById(R.id.time)
            allow = innerView.findViewById(R.id.allowed)
            delete = innerView.findViewById(R.id.delete)
        }
    }
}