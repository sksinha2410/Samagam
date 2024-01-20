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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ingray.samagam.DataClass.Alumni
import com.ingray.samagam.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Calendar

class AddAlumniActivity : AppCompatActivity() {
    private lateinit var  name:EditText
    private lateinit var batchyear:Spinner
    private lateinit var branch:EditText
    private lateinit var postinclub:EditText
    private lateinit var phoneno:EditText
    private lateinit var email:EditText
    private lateinit var linkedin:EditText
    private lateinit var github:EditText
    private lateinit var discord:EditText
    private lateinit var insta:EditText
    private lateinit var twitter:EditText
    private lateinit var position:EditText
    private lateinit var location:EditText
    private lateinit var discreption:EditText
    private lateinit var achievement:EditText
    private lateinit var company:EditText
    val Pick_image=1
    private lateinit var profile:ImageView
    private lateinit var btnSubmit:Button
    private lateinit var selectedItem:String
    private lateinit var purl:String
    private lateinit var progress:ProgressBar
    private var deRef : DatabaseReference = FirebaseDatabase.getInstance().reference
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alumni)
        var clubName = intent.getStringExtra("Clubname")


        callById()
        profile.setOnClickListener{
            openGallery()
        }
        getItemFromSpinner()
        btnSubmit.setOnClickListener{
            val ad = Alumni()
            ad.batch = selectedItem
            ad.branch = branch.text.toString()
            ad.company = company.text.toString()
            ad.description = discreption.text.toString()
            ad.discord = discord.text.toString()
            ad.email = email.text.toString()
            ad.github = github.text.toString()
            ad.instagram = insta.text.toString()
            ad.linkedIn = linkedin.text.toString()
            ad.name = name.text.toString()
            ad.postInClub = postinclub.text.toString()
            ad.phoneNo = phoneno.text.toString()
            ad.purl = purl
            ad.achievements=achievement.text.toString()
            ad.placeOfWork=location.text.toString()
            ad.twitter=twitter.text.toString()
            ad.position=position.text.toString()
            if (clubName != null) {
                deRef.child("Clubs").child(clubName).child("Alumni").child(selectedItem).child("Members").setValue(ad)
                Toast.makeText(this,"Member added",Toast.LENGTH_SHORT).show()
                finish()
            }
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
        val data= arrayOf("2021-2025","2020-2024","2019-2023","2018-2022","2017-2021","2016-2020","2015-2019","2014-2018","2013-2017","2012-2016",)
        val adapt= ArrayAdapter(this,android.R.layout.simple_spinner_item,data)
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        batchyear.adapter=adapt
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