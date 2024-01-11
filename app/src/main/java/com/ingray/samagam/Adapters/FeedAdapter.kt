package com.ingray.samagam.Adapters

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
import com.ingray.samagam.DataClass.Posts
import com.ingray.samagam.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar
import java.util.concurrent.TimeUnit

class FeedAdapter(options: FirebaseRecyclerOptions<Posts?>) :
    FirebaseRecyclerAdapter<Posts?, FeedAdapter.userAdapterHolder?>(options) {

        private var dbRef:DatabaseReference = FirebaseDatabase.getInstance().reference.child("Posts")
        private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        private var bool:Boolean = false
        private lateinit var ref:DatabaseReference
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

        dbRef.child(model.time).child("LikedBy").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (checkUser in snapshot.children){
                    if(checkUser.exists()){
                        val checkID = checkUser.child("userId").value.toString()
                        if (checkID == userId){
                            holder.likes.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.red_heart,
                                0,
                                0,
                                0
                            )
                            bool=true
                            ref = checkUser.ref
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        holder.likes.setOnClickListener{
            var oldLikes =model.likes.toLong()
            if (bool){
                oldLikes -= 1
                var map= hashMapOf<String,String>()
                map.set("likes", oldLikes.toString())
                dbRef.child(model.time).updateChildren(map as Map<String, Any>)
                ref.removeValue()
                bool=false
                holder.likes.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.white_heart,
                    0,
                    0,
                    0
                )
            }
            else{
                oldLikes += 1
                var map= hashMapOf<String,String>()
                map.set("likes", oldLikes.toString())
                dbRef.child(model.time).updateChildren(map as Map<String, Any>)
                var map2 = hashMapOf<String, String>()

                map2.set("userId", userId)
                dbRef.child(model.time).child("LikedBy").child(userId).setValue(map2)
            }
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
}