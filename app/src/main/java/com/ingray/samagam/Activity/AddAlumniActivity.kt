package com.ingray.samagam.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ingray.samagam.R

class AddAlumniActivity : AppCompatActivity() {
    private lateinit var  name:EditText
    private lateinit var batchyear:EditText
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
    private lateinit var profile:ImageView
    private lateinit var btnSubmit:Button
    private lateinit var spinner:Spinner
    private var deRef : DatabaseReference = FirebaseDatabase.getInstance().reference
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alumni)
        
        callById()
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
        btnSubmit = findViewById(R.id.btn_submit)
        spinner = findViewById(R.id.spinner)
    }
}