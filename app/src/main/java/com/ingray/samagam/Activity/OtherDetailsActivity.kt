package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.ingray.samagam.R

class OtherDetailsActivity : AppCompatActivity() {
    private lateinit var username: TextInputEditText
    private lateinit var enter: MaterialButton
    private lateinit var sUser: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_details)

        // Initialize the TextInputEditText using findViewById
        username = findViewById(R.id.etUsername)
        enter = findViewById(R.id.btnEnter)
        enter.setOnClickListener{
            sUser = username.text.toString()
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}
