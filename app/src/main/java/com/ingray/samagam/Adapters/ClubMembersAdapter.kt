package com.ingray.samagam.Adapters
import android.content.Intent
import android.net.Uri
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
import com.ingray.samagam.DataClass.Alumni
import de.hdodenhof.circleimageview.CircleImageView

class ClubMembersAdapter(options: FirebaseRecyclerOptions<Alumni?>) :
    FirebaseRecyclerAdapter<Alumni?, ClubMembersAdapter.userAdapterHolder?>(options) {
    lateinit var view: View
    override fun onBindViewHolder(holder: userAdapterHolder,
                                  position: Int,
                                  model: Alumni) {
        holder.name.text=model.name
        holder.position.text=model.position
        holder.company.text=model.company
        holder.batch.text=model.batch
        holder.postinClub.text=model.postInClub
        holder.placeofWork.text=model.placeOfWork
        holder.description.text=model.description
        holder.achievement.text=model.achievements
        Glide.with(holder.profile.context).load(model.purl).into(holder.profile)

        checkCondition(holder,model)

        callOnClick(holder,model)





    }

    private fun callOnClick(holder: ClubMembersAdapter.userAdapterHolder, model: Alumni) {
        holder.github.setOnClickListener {
            val xmllink = model.github
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(xmllink))
                holder.itemView.context.startActivity(intent)
            }catch (
                e:Exception
            )
            {Toast.makeText(holder.github.context,"Link invalid", Toast.LENGTH_SHORT).show()

            }
        }
        holder.insta.setOnClickListener {
            val xmllink = model.instagram
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(xmllink))
                holder.itemView.context.startActivity(intent)
            }catch (
                e:Exception
            )
            {Toast.makeText(holder.github.context,"Link invalid", Toast.LENGTH_SHORT).show()

            }
        }

        holder.linkedin.setOnClickListener {
            val xmllink = model.linkedIn
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(xmllink))
                holder.itemView.context.startActivity(intent)
            }catch (
                e:Exception
            )
            {Toast.makeText(holder.github.context,"Link invalid", Toast.LENGTH_SHORT).show()

            }
        }

        holder.email.setOnClickListener {
            val emailLink = "mailto:${model.email}"
            try {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse(emailLink)
                holder.itemView.context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(holder.itemView.context, "Error opening email", Toast.LENGTH_SHORT).show()
            }
        }

        holder.descord.setOnClickListener {
            val xmllink = model.discord
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(xmllink))
                holder.itemView.context.startActivity(intent)
            }catch (
                e:Exception
            )
            {Toast.makeText(holder.github.context,"Link invalid", Toast.LENGTH_SHORT).show()

            }
        }


        holder.twitter.setOnClickListener {
            val xmllink = model.twitter
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(xmllink))
                holder.itemView.context.startActivity(intent)
            }catch (
                e:Exception
            )
            {Toast.makeText(holder.github.context,"Link invalid", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun checkCondition(holder: ClubMembersAdapter.userAdapterHolder, model: Alumni) {
        if(model.name.isEmpty()){
            holder.name.visibility = View.GONE
        }
        if(model.position.isEmpty()){
            holder.position.visibility = View.GONE
        }
        if(model.company.isEmpty()){
            holder.company.visibility = View.GONE
        }
        if(model.batch.isEmpty()){
            holder.batch.visibility = View.GONE
        }
        if(model.postInClub.isEmpty()){
            holder.postinClub.visibility = View.GONE
        }
        if(model.placeOfWork.isEmpty()){
            holder.placeofWork.visibility = View.GONE
        }
        if(model.description.isEmpty()){
            holder.description.visibility = View.GONE
        }
        if(model.achievements.isEmpty()){
            holder.achievement.visibility = View.GONE
        }
        if(model.purl.isEmpty()){
            holder.profile.visibility = View.GONE
        }
        if(model.github.isEmpty()){
            holder.github.visibility = View.GONE
        }
        if(model.instagram.isEmpty()){
            holder.insta.visibility = View.GONE
        }
        if(model.linkedIn.isEmpty()){
            holder.linkedin.visibility = View.GONE
        }
        if(model.twitter.isEmpty()){
            holder.twitter.visibility = View.GONE
        }
        if(model.email.isEmpty()){
            holder.email.visibility = View.GONE
        }
        if(model.discord.isEmpty()){
            holder.descord.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userAdapterHolder {
        view =LayoutInflater.from(parent.context).inflate(R.layout.alumni_item, parent, false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile: CircleImageView
        var github: TextView
        var insta: TextView
        var twitter: TextView
        var descord: TextView
        var email: TextView
        var linkedin: TextView
        var name: TextView
        var position: TextView
        var company: TextView
        var batch: TextView
        var postinClub: TextView
        var placeofWork: TextView
        var description: TextView
        var achievement: TextView
        
        init {
            profile= itemView.findViewById<CircleImageView>(R.id.profile)
           github= itemView.findViewById<TextView>(R.id.git)
            insta= itemView.findViewById<TextView>(R.id.insta)
            twitter= itemView.findViewById<TextView>(R.id.twitter)
            descord= itemView.findViewById<TextView>(R.id.descord)
            email= itemView.findViewById<TextView>(R.id.email)
            linkedin= itemView.findViewById<TextView>(R.id.linkedin)
            name = itemView.findViewById<TextView>(R.id.name)
            position = itemView.findViewById<TextView>(R.id.position)
            company = itemView.findViewById<TextView>(R.id.company)
            batch = itemView.findViewById<TextView>(R.id.batch)
            postinClub= itemView.findViewById<TextView>(R.id.postinClub)
            placeofWork = itemView.findViewById<TextView>(R.id.placeofWork)
            description= itemView.findViewById<TextView>(R.id.description)
            achievement= itemView.findViewById<TextView>(R.id.achievement)
          
        }
    }
}