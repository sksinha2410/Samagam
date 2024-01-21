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
import com.google.firebase.database.FirebaseDatabase
import com.ingray.samagam.Activity.ClubDetailsActivity
import com.ingray.samagam.Activity.ClubMembersDetailActivity
import com.ingray.samagam.Activity.EventsOfClubsActivity
import com.ingray.samagam.DataClass.Alumni
import com.ingray.samagam.DataClass.MembersProfile

class AlumniBatchAdapter(options: FirebaseRecyclerOptions<MembersProfile?>, val clubName: String) :
    FirebaseRecyclerAdapter<MembersProfile?, AlumniBatchAdapter.userAdapterHolder?>(options) {
    lateinit var view: View
    override fun onBindViewHolder(holder: userAdapterHolder,
                                  position: Int,
                                  model: MembersProfile) {
        Glide.with(holder.profile1.context).load(model.profile1).into(holder.profile1)
        Glide.with(holder.profile2.context).load(model.profile2).into(holder.profile2)
        Glide.with(holder.profile3.context).load(model.profile3).into(holder.profile3)
        Glide.with(holder.profile4.context).load(model.profile4).into(holder.profile4)
        holder.batchName.text=model.batch
        holder.cvAll.setOnClickListener{
             val intent=Intent(holder.cvAll.context,ClubMembersDetailActivity::class.java)
            intent.putExtra("clubName",clubName)
            intent.putExtra("batch",model.batch)
            intent.putExtra("type","Alumni")
             view.context.startActivity(intent)
         }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userAdapterHolder {
        view =LayoutInflater.from(parent.context).inflate(R.layout.alumni_batch_item, parent, false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profile1: ImageView
        var profile2: ImageView
        var profile3: ImageView
        var profile4: ImageView

        var batchName: TextView
        var cvAll: CardView
        init {
            profile1 = itemView.findViewById<ImageView>(R.id.profile1)
            profile2 = itemView.findViewById<ImageView>(R.id.profile2)
            profile3 = itemView.findViewById<ImageView>(R.id.profile3)
            profile4 = itemView.findViewById<ImageView>(R.id.profile4)
            batchName = itemView.findViewById<TextView>(R.id.batchName)
            cvAll = itemView.findViewById(R.id.cvAll)
        }
    }
}