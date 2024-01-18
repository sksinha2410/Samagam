package com.ingray.samagam.Fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ingray.samagam.Activity.AddEventsActivity
import com.ingray.samagam.Activity.AddPostsActivity
import com.ingray.samagam.Activity.LoginActivity
import com.ingray.samagam.Activity.ReportedPost
import com.ingray.samagam.Adapters.ProfilePostAdapter
import com.ingray.samagam.DataClass.Posts
import com.ingray.samagam.DataClass.Users
import com.ingray.samagam.R
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProfileFragment : Fragment() {
    private lateinit var profileImage : CircleImageView
    private lateinit var profileName : TextView
    private lateinit var addEvent:TextView
    var deRef = FirebaseDatabase.getInstance().getReference("Users")
    private lateinit var view :View
    private lateinit var recyclerPost: RecyclerView
    private lateinit var ppAdapter: ProfilePostAdapter
    private lateinit var logout:LinearLayout
    private lateinit var addPost:LinearLayout
    val Pick_image=1
    var storageReference = FirebaseStorage.getInstance().reference
    lateinit var purl:String
    lateinit var admin:String
    private lateinit var progress: ProgressBar
    private lateinit var report:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       view = inflater.inflate(R.layout.fragment_profile, container, false)
        callById()
        progress.visibility = View.INVISIBLE

        deRef.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
             var us : Users
             if(snapshot.exists()){
                    us = snapshot.getValue(Users::class.java)!!
                    profileName.setText(us.name)
                    admin = us.userType
                    if (admin == "0"){
                        addEvent.visibility = View.GONE
                        report.visibility =View.GONE
                    }else if(admin =="1"){
                        addEvent.visibility = View.VISIBLE
                        report.visibility = View.VISIBLE
                    }
                    else{
                        addEvent.visibility = View.VISIBLE
                        report.visibility = View.GONE
                    }

                    if (us.purl.isEmpty()){
                        profileImage.setImageResource(R.drawable.add_photo)
                    }else{
                        Glide.with(view.context).load(us.purl).into(profileImage)
                    }
             }
            }

                override fun onCancelled(error: DatabaseError) {
            // Handle errors here
            Log.e("Firebase", "Error fetching events: ${error.message}")
        }
    })
        recyclerPost.itemAnimator = null
        val options: FirebaseRecyclerOptions<Posts?> = FirebaseRecyclerOptions.Builder<Posts>().
        setQuery(deRef.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("Posts"), Posts::class.java).build()
        ppAdapter = ProfilePostAdapter(options)
        recyclerPost.adapter = ppAdapter
        ppAdapter.startListening()
        profileImage.setOnClickListener{
            openGallery()
        }
        addPost.setOnClickListener{
            val intent = Intent(view.context,AddPostsActivity::class.java)
            view.context.startActivity(intent)
        }

        addEvent.setOnClickListener{
            val intent = Intent(view.context,AddEventsActivity::class.java)
            intent.putExtra("userType",admin)
            view.context.startActivity(intent)
        }
        report.setOnClickListener{
            val intent = Intent(view.context,ReportedPost::class.java)
            view.context.startActivity(intent)
        }
        logout.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            builder

                .setTitle("Do You surely want to logout?")
                .setPositiveButton("Yes") { dialog, which ->

                    val intent = Intent(
                        activity, LoginActivity::class.java
                    )
                    startActivity(intent)
                    Toast.makeText(view.context,"Logged Out", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()

                }
                .setNegativeButton("No") { dialog, which ->
                    // Do something else.
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()



        }
        return view
    }

    private fun openGallery() {
        val gallery=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(gallery,Pick_image)

    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Pick_image && resultCode == RESULT_OK && data != null) {
            val resultUri: Uri = data.data!!
            progress.visibility = View.VISIBLE
            uploadImageToFirebase(resultUri)
            profileImage.setImageURI(resultUri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun uploadImageToFirebase(imageUri: Uri) {
        val fileRef: StorageReference =
            storageReference.child("users/" + FirebaseAuth.getInstance().currentUser?.uid + "profile.jpg")

        // Load the image into a Bitmap
        val bitmap: Bitmap
        try {
            // Assuming imageUri is a valid URI
            val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
            bitmap = ImageDecoder.decodeBitmap(source)
            // Use the bitmap here...
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

// Compress the image with reduced quality (adjust quality as needed)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos) // Adjust the quality here (50 in this example)

        // Convert the compressed Bitmap to bytes
        val data = baos.toByteArray()

        // Upload the compressed image to Firebase Storage
        val uploadTask = fileRef.putBytes(data)
        uploadTask.addOnSuccessListener { // Handle the successful upload
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                purl = uri.toString()
                val users: MutableMap<String, Any> =
                    HashMap()

                users["purl"] = purl
                try {
                    if (purl != null) {
                        deRef.child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .updateChildren(users)

                    }
                } catch (e: Exception) {
                }
                Glide.with(view.context).load(purl).into(profileImage)
                progress.visibility = View.INVISIBLE
            }
        }.addOnFailureListener { // Handle the failure to upload
            Toast.makeText(view.context, "Failed.", Toast.LENGTH_LONG).show()
            progress.visibility = View.INVISIBLE
        }
    }

    private fun callById() {
        profileImage = view.findViewById(R.id.profileImage)
        profileName = view.findViewById(R.id.profileName)
        addEvent = view.findViewById(R.id.addEvent)
        recyclerPost=view.findViewById(R.id.profile_recycler)
        logout = view.findViewById(R.id.logout)
        addPost = view.findViewById(R.id.addPost)
        progress =view.findViewById(R.id.sale_progressBar)
        report=view.findViewById(R.id.allReports)
    }
}