package com.ingray.samagam.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ingray.samagam.DataClass.Clubs
import com.ingray.samagam.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
class ClubsAdapter(options: FirebaseRecyclerOptions<Clubs?>) :
    FirebaseRecyclerAdapter<Clubs?, ClubsAdapter.userAdapterHolder?>(options) {
    override fun onBindViewHolder(holder: userAdapterHolder, position: Int, model: Clubs) {
        try {
            holder.name.setText(model.club_name)

            Glide.with(holder.profileImage.context).load(model.imageUrl).into(holder.profileImage)
        } catch (e: Exception) {
            Toast.makeText(holder.name.context, "Something wrong in Developer", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userAdapterHolder {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.club_item, parent, false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: ImageView
        var name: TextView
        init {
            profileImage = itemView.findViewById<ImageView>(R.id.ivLogo)
            name = itemView.findViewById<TextView>(R.id.tv_title)
        }
    }
}


