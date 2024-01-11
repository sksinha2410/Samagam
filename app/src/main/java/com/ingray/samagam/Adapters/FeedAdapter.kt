package com.ingray.samagam.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.ingray.samagam.DataClass.Posts
import de.hdodenhof.circleimageview.CircleImageView
import java.util.concurrent.TimeUnit

class FeedAdapter(options: FirebaseRecyclerOptions<Posts?>) :
    FirebaseRecyclerAdapter<Posts?, FeedAdapter.userAdapterHolder?>(options) {
    override fun onBindViewHolder(
        holder: userAdapterHolder,
        position: Int,
        model: Posts
    ) {
        Toast.makeText(holder.itemView.context,"Entered Feed",Toast.LENGTH_LONG).show()
        holder.username.setText(model.username)
        val previousTimeMillis =model.time.toLong()

        val currentTimeMillis = System.currentTimeMillis()

        val timeDifferenceMillis = currentTimeMillis - previousTimeMillis

        val hoursDifference = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)

        holder.time.text = hoursDifference.toString()

        holder.likes.setText(model.likes)
        Glide.with(holder.postImage.context).load(model.postUrl).into(holder.postImage)
        Glide.with(holder.profileImage.context).load(model.purl).into(holder.profileImage)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): userAdapterHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.feed_post_item,parent,false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(innerView:View):RecyclerView.ViewHolder(innerView) {
        var profileImage: CircleImageView
        var postImage:ImageView
        var username:TextView
        var time:TextView
        var likes:TextView

        init {
            profileImage = innerView.findViewById(R.id.profileImage)
            postImage = innerView.findViewById(R.id.postImage)
            username = innerView.findViewById(R.id.username)
            time = innerView.findViewById(R.id.time)
            likes = innerView.findViewById(R.id.upvote)

        }
    }
}