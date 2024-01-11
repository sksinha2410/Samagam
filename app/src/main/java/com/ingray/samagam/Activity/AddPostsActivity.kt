package com.ingray.samagam.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ingray.samagam.DataClass.Posts
import com.ingray.samagam.DataClass.Users
import de.hdodenhof.circleimageview.CircleImageView
import com.ingray.samagam.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Calendar

class AddPostsActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var upvoteTextView: TextView
    private lateinit var shareTextView: TextView

    // ImageViews
    private lateinit var profileImageView: CircleImageView
    private lateinit var postImageView: ImageView

    // Spinner
    private lateinit var spinner: Spinner
    val Pick_image=1
    private var purl:String = ""
    private lateinit var prourl:String

    val cal = Calendar.getInstance().timeInMillis
    val timestr = cal.toString()

    // Button
    private lateinit var submitButton: Button

    private var dbRef:DatabaseReference = FirebaseDatabase.getInstance().reference
    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var userId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_posts)


        callById()
        assignUser()
        postImageView.setOnClickListener{
            openGallery()

        }
        submitButton.setOnClickListener{
            if (!purl.isEmpty()){
                val post:Posts = Posts()
                post.postId = timestr
                post.postUrl= purl
                post.likes = "0"
                post.username = usernameTextView.text.toString()
                post.time = timestr
                post.purl = prourl
                post.userId = userId

                dbRef.child("Users").child(userId).child("Posts").child(timestr).setValue(post)
                dbRef.child("Posts").child(timestr).setValue(post)
                Toast.makeText(applicationContext,"Post added",Toast.LENGTH_LONG).show()
                finish()
            }
           else{
                Toast.makeText(applicationContext,"Select Post Image",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun openGallery() {
        val gallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(gallery,Pick_image)

    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Pick_image && resultCode == RESULT_OK && data != null) {
            val resultUri: Uri = data.data!!
            uploadImageToFirebase(resultUri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun uploadImageToFirebase(imageUri: Uri) {
        val fileRef: StorageReference =
            storageReference.child("posts/" +"post"+ timestr + "post.jpg")

        // Load the image into a Bitmap
        val bitmap: Bitmap
        try {
            // Assuming imageUri is a valid URI
            val source = ImageDecoder.createSource(contentResolver, imageUri)
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

                Glide.with(applicationContext).load(purl).into(postImageView)
            }
        }.addOnFailureListener { // Handle the failure to upload
            Toast.makeText(applicationContext, "Failed.", Toast.LENGTH_LONG).show()
        }
    }


    private fun assignUser() {
        dbRef.child("Users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var us: Users
                if (snapshot.exists()) {
                    us = snapshot.getValue(Users::class.java)!!
                    usernameTextView.setText(us.name)
                    Glide.with(applicationContext).load(us.purl).into(profileImageView)
                    prourl = us.purl

                    timeTextView.setText(timestr)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
                Log.e("Firebase", "Error fetching events: ${error.message}")
            }
        })
    }

    fun callById() {
        usernameTextView = findViewById(R.id.username)
        timeTextView = findViewById(R.id.time)
        upvoteTextView = findViewById(R.id.upvote)
        shareTextView = findViewById(R.id.share)

        // Initialize ImageViews
        profileImageView = findViewById(R.id.profileImage)
        postImageView = findViewById(R.id.postImage)

        // Initialize Spinner
        spinner = findViewById(R.id.spinner)

        // Initialize Button
        submitButton = findViewById(R.id.btn_submit)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    }
}
