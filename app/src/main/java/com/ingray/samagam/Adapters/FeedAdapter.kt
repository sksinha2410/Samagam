package com.ingray.samagam.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.ingray.samagam.DataClass.Posts
import com.ingray.samagam.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar
import java.util.concurrent.TimeUnit


class FeedAdapter(options: FirebaseRecyclerOptions<Posts?>) :
    FirebaseRecyclerAdapter<Posts?, FeedAdapter.userAdapterHolder?>(options) {

    private var dbRef:DatabaseReference = FirebaseDatabase.getInstance().reference.child("Posts")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()


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

        holder.likes.setText(model.likes)
        Glide.with(holder.postImage.context).load(model.postUrl).into(holder.postImage)
        Glide.with(holder.profileImage.context).load(model.purl).into(holder.profileImage)
        val likedByRef= dbRef.child(model.postId).child("LikedBy")

        likedByRef!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    if (snapshot.child(userId).exists()){
                        var b = snapshot.child(userId).getValue(Boolean::class.java)
                        if (snapshot.hasChild(userId) && b.toString() == "true"){
                            holder.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_heart,0,0,0)
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        holder.likes.setOnClickListener {
            handleLikeButtonClick(holder,model,likedByRef)
        }

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

    private fun handleLikeButtonClick(holder: userAdapterHolder,model:Posts,likedByRef:DatabaseReference) {
        likedByRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                if (dataSnapshot.hasChild(userId)) {
                    // User has previously liked the post, toggle the value
                    val currentLikeStatus = dataSnapshot.child(userId).getValue(Boolean::class.java)
                    if (currentLikeStatus==true){
                        holder.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.white_heart,0,0,0)
                    }else{
                        holder.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_heart,0,0,0)
                    }
                    likedByRef!!.child(userId).setValue(!(currentLikeStatus ?: false)).addOnCompleteListener{
                        if (it.isSuccessful){
                            var trueCount = 0

                            for (childSnapshot in dataSnapshot.children) {
                                // Check if the value is true
                                val value = childSnapshot.getValue(Boolean::class.java)
                                if (value != null && value) {
                                    trueCount++
                                }
                            }

                            holder.likes.text = trueCount.toString()
                            dbRef.child(model.postId).child("likes").setValue(trueCount.toString())
                        }
                    }
                    Log.d("FirebaseDemo", "Like status toggled")

                } else {
                    // User is liking the post for the first time
                    likedByRef!!.child(userId).setValue(true).addOnCompleteListener {
                        dbRef.child(model.postId).child("likes").setValue("1")
                    }
                    Log.d("FirebaseDemo", "Liked for the first time")
                    holder.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.red_heart,0,0,0)

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDemo", "Error updating like status: ${databaseError.message}")
            }
        })

    }
}