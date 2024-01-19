package com.ingray.samagam.Adapters
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ingray.samagam.DataClass.Clubs
import com.ingray.samagam.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.ingray.samagam.Activity.ClubDetailsActivity
import com.ingray.samagam.Activity.EventsOfClubsActivity

class ClubsAdapter(options: FirebaseRecyclerOptions<Clubs?>) :
    FirebaseRecyclerAdapter<Clubs?, ClubsAdapter.userAdapterHolder?>(options) {
    lateinit var view: View
    override fun onBindViewHolder(holder: userAdapterHolder,
                                  position: Int,
                                  model: Clubs) {
        try {
            holder.name.text = model.club_name

            Glide.with(holder.profileImage.context).load(model.imageUrl).into(holder.profileImage)
            holder.clubCard.setOnClickListener{
                val intent = Intent(view.context,ClubDetailsActivity::class.java)
                intent.putExtra("clubName", model.club_name) // Pass any necessary data to the new activity
                holder.itemView.context.startActivity(intent)

            }
        } catch (e: Exception) {
            Toast.makeText(holder.name.context, "Something wrong in Developer", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userAdapterHolder {
        view =LayoutInflater.from(parent.context).inflate(R.layout.club_item, parent, false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: ImageView
        var name: TextView
        var clubCard: CardView
        init {
            profileImage = itemView.findViewById<ImageView>(R.id.ivLogo)
            name = itemView.findViewById<TextView>(R.id.tv_title)
            clubCard = itemView.findViewById(R.id.clubCard)
        }
    }
}