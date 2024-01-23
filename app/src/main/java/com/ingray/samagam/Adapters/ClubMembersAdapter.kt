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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.Activity.ClubDetailsActivity
import com.ingray.samagam.Activity.MembersDetailsEditActivity
import com.ingray.samagam.DataClass.Alumni
import de.hdodenhof.circleimageview.CircleImageView

class ClubMembersAdapter(options: FirebaseRecyclerOptions<Alumni?>,val clubName:String) :
    FirebaseRecyclerAdapter<Alumni?, ClubMembersAdapter.userAdapterHolder?>(options) {
    lateinit var view: View
    override fun onBindViewHolder(holder: userAdapterHolder,
                                  position: Int,
                                  model: Alumni) {
        holder.name.text=model.name
        holder.position.text=model.position
        holder.company.text=model.company
        holder.batch.text=model.batch
        holder.branch.text=model.branch
        holder.postinClub.text=model.postInClub
        holder.description.text=model.description
        holder.achievement.text=model.achievements
        Glide.with(holder.profile.context).load(model.purl).into(holder.profile)
        if (!model.userId.equals(null)&&!model.userId.equals("")&&FirebaseAuth.getInstance().currentUser?.uid.toString().equals(model.userId)){
            holder.edit.visibility = View.VISIBLE
        }
        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        if(snapshot.child("userType").value.toString() == "1"){
                            holder.edit.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        holder.edit.setOnClickListener{
            val intent = Intent(holder.itemView.context,MembersDetailsEditActivity::class.java)
            intent.putExtra("clubname",clubName)
            intent.putExtra("name",model.name)
            intent.putExtra("branch",model.branch)
            intent.putExtra("position",model.position)
            intent.putExtra("company",model.company)
            intent.putExtra("achievements",model.achievements)
            intent.putExtra("email",model.email)
            intent.putExtra("linkedin",model.linkedIn)
            intent.putExtra("twitter",model.twitter)
            intent.putExtra("git",model.github)
            intent.putExtra("insta",model.instagram)
            intent.putExtra("discord",model.discord)
            intent.putExtra("postinclub",model.postInClub)
            intent.putExtra("phone",model.phoneNo)
            intent.putExtra("purl",model.position)
            intent.putExtra("description",model.description)
            holder.itemView.context.startActivity(intent)
        }

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

        if(model.description.isEmpty()){
            holder.description.visibility = View.GONE
        }
        if(model.achievements.isEmpty()){
            holder.achievement.visibility = View.GONE
        }
        if(model.purl.isEmpty()){
            holder.profile.visibility = View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userAdapterHolder {
        view =LayoutInflater.from(parent.context).inflate(R.layout.club_member_item, parent, false)
        return userAdapterHolder(view)
    }

    inner class userAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile: CircleImageView
        var github: ImageView
        var insta: ImageView
        var twitter: ImageView
        var descord: ImageView
        var email: ImageView
        var linkedin: ImageView
        var name: TextView
        var position: TextView
        var company: TextView
        var batch: TextView
        var branch: TextView
        var postinClub: TextView
        var description: TextView
        var achievement: TextView
        var edit: ImageView

        init {
            profile= itemView.findViewById<CircleImageView>(R.id.profile)
           github= itemView.findViewById(R.id.git)
            insta= itemView.findViewById(R.id.insta)
            twitter= itemView.findViewById(R.id.twitter)
            descord= itemView.findViewById(R.id.descord)
            email= itemView.findViewById(R.id.email)
            linkedin= itemView.findViewById(R.id.linkedin)
            name = itemView.findViewById(R.id.name)
            position = itemView.findViewById(R.id.position)
            company = itemView.findViewById(R.id.company)
            batch = itemView.findViewById(R.id.batch)
            branch = itemView.findViewById(R.id.branch)
            postinClub= itemView.findViewById(R.id.postinClub)
            description= itemView.findViewById(R.id.description)
            achievement= itemView.findViewById(R.id.achievement)
            edit = itemView.findViewById(R.id.editDetails)
          
        }
    }
}