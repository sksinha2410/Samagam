package com.ingray.samagam.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
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
import com.ingray.samagam.DataClass.Alumni
import com.ingray.samagam.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Calendar

class MembersDetailsEditActivity : AppCompatActivity() {
    private lateinit var  name: EditText
    private lateinit var batchyear: Spinner
    private lateinit var branch: EditText
    private lateinit var postinclub: EditText
    private lateinit var phoneno: EditText
    private lateinit var email: EditText
    private lateinit var linkedin: EditText
    private lateinit var github: EditText
    private lateinit var discord: EditText
    private lateinit var insta: EditText
    private lateinit var twitter: EditText
    private lateinit var position: EditText
    private lateinit var location: EditText
    private lateinit var discreption: EditText
    private lateinit var achievement: EditText
    private lateinit var company: EditText
    val Pick_image=1
    private lateinit var profile: ImageView
    private lateinit var btnSubmit: Button
    private lateinit var selectedItem:String
    private var purl:String =""
    private var type = ""
    private lateinit var progress: ProgressBar
    private var deRef : DatabaseReference = FirebaseDatabase.getInstance().reference
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members_details_edit)


        val intent = intent
        val clubname = intent.getStringExtra("clubname")
        val name = intent.getStringExtra("name")
        val branch =intent.getStringExtra("branch")
        val position =intent.getStringExtra("position")
        val company =intent.getStringExtra("company")
        val achievement =intent.getStringExtra("achievements")
        val email =intent.getStringExtra("email")
        val linkedin =intent.getStringExtra("linkedin")
        val twitter =intent.getStringExtra("twitter")
        val git = intent.getStringExtra("git")
        val insta =intent.getStringExtra("insta")
        val discord =intent.getStringExtra("discord")
        val postInClub =intent.getStringExtra("postinclub")
        val phone =intent.getStringExtra("phone")
        purl =intent.getStringExtra("purl")!!
        val description=intent.getStringExtra("description")

        callById()
        getItemFromSpinner()

        this.email.setText(email)
        this.name.setText(name)
        this.branch.setText(branch)
        this.position.setText(position)
        this.company.setText(company)
        this.achievement.setText(achievement)
        this.linkedin.setText(linkedin)
        this.twitter.setText(twitter)
        this.github.setText(git)
        this.insta.setText(insta)
        this.discord.setText(discord)
        this.postinclub.setText(postInClub)
        this.phoneno.setText(phone)
        this.discreption.setText(description)

        Glide.with(applicationContext).load(intent.getStringExtra("purl")!!).into(profile)

        profile.setOnClickListener{
            openGallery()
        }

        btnSubmit.setOnClickListener{

            val map = HashMap<String,String>()
            map.put("batch",selectedItem)
            map.put("branch",this.branch.text.toString())
            map.put("company",this.company.text.toString())
            map.put("description",discreption.text.toString())
            map.put("discord",this.discord.text.toString())
            map.put("email",this.email.text.toString())
            map.put("github",github.text.toString())
            map.put("instagram",this.insta.text.toString())
            map.put("linkedIn",this.linkedin.text.toString())
            map.put("name",this.name.text.toString())
            map.put("postInClub",postinclub.text.toString())
            map.put("phoneNo",phoneno.text.toString())
            map.put("purl",purl)
            map.put("twitter",this.twitter.text.toString())
            map.put("position",this.position.text.toString())
            map.put("achievements",this.achievement.text.toString())
            map.put("placeOfWork",this.location.text.toString())
            deRef.child("Clubs").child(clubname!!).child("OfficeBearer")
                .child("Members").addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshots: DataSnapshot) {
                        for (snapshot in snapshots.children) {
                            if (snapshot.child("userId")
                                    .exists() && snapshot.child("userId").value.toString().equals(
                                    FirebaseAuth.getInstance().currentUser?.uid.toString()
                                )
                            ) {
                                val key = snapshot.key.toString()
                                deRef.child("Clubs").child(clubname!!).child("OfficeBearer")
                                    .child("Members").child(key).updateChildren(map as Map<String, Any>)
                                Toast.makeText(applicationContext,"Updated",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            Toast.makeText(this, "Member added", Toast.LENGTH_SHORT).show()
            finish()
        }


    }


    private fun callById() {
        email = findViewById(R.id.etEmail)
        name = findViewById(R.id.etName)
        batchyear = findViewById(R.id.etBatch)
        branch = findViewById(R.id.etBranch)
        postinclub = findViewById(R.id.etPostinClub)
        phoneno = findViewById(R.id.etPhoneNo)
        linkedin = findViewById(R.id.etLinkedIn)
        github = findViewById(R.id.etGitHub)
        discord = findViewById(R.id.etDiscord)
        insta = findViewById(R.id.etInstagram)
        twitter = findViewById(R.id.etTwitter)
        position = findViewById(R.id.etPosition)
        location = findViewById(R.id.etLocation)
        discreption = findViewById(R.id.description)
        achievement = findViewById(R.id.etAchievement)
        company = findViewById(R.id.etCompany)
        profile = findViewById(R.id.profile)
        progress = findViewById(R.id.progress)
        btnSubmit = findViewById(R.id.btn_submit)

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
            progress.visibility = View.VISIBLE
            uploadImageToFirebase(resultUri)

            profile.setImageURI(resultUri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun uploadImageToFirebase(imageUri: Uri) {
        val fileRef: StorageReference =
            storageRef.child("PosterEvent/" + Calendar.getInstance().timeInMillis.toString() + "profile.jpg")

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
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            60,
            baos
        ) // Adjust the quality here (50 in this example)

        // Convert the compressed Bitmap to bytes
        val data = baos.toByteArray()

        // Upload the compressed image to Firebase Storage
        val uploadTask = fileRef.putBytes(data)
        uploadTask.addOnSuccessListener { // Handle the successful upload
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                purl = uri.toString()
                Glide.with(applicationContext).load(purl).into(profile)
                progress.visibility = View.INVISIBLE
            }
        }.addOnFailureListener { // Handle the failure to upload
            Toast.makeText(applicationContext, "Failed.", Toast.LENGTH_LONG).show()
            progress.visibility = View.INVISIBLE
        }
    }
    private fun getItemFromSpinner() {
        val data = arrayOf(
            "2023-2027",
            "2022-2026",
            "2021-2025",
            "2020-2024"

        )
        val adapt = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        batchyear.adapter = adapt
        batchyear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedItem = parent.getItemAtPosition(position).toString()
                // Handle the selected item
                Toast.makeText(applicationContext,selectedItem, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle no selection
            }
        }
    }
}