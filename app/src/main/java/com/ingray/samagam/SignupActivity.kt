package com.ingray.samagam

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    //Initialising all the variables
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPass: EditText
    private lateinit var signin: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var sEmail:String
    private lateinit var sPass:String
    private lateinit var cPass:String



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        callVariablesByIds()

        setOnClickListeners()


    }

    private fun setOnClickListeners() {
        signin.setOnClickListener {
            sEmail = email.text.toString()
            sPass = password.text.toString()
            cPass = confirmPass.text.toString()

            checkAllTheConditions(sEmail,sPass,cPass)

            auth.createUserWithEmailAndPassword(sEmail,sPass).
            addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
//                    updateUI(null)
                }
            }
        }

    }

    private fun checkAllTheConditions(email:String, password:String,confirmPass:String) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
        } else if (!email.contains("nitp.ac.in")) {
            Toast.makeText(this, "Enter Your College email", Toast.LENGTH_SHORT)
                .show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Confirm your password", Toast.LENGTH_SHORT).show()
        } else if (password != confirmPass) {
            Toast.makeText(
                this,
                "Password & confirm password must be same",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun callVariablesByIds() {
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPass)
        signin= findViewById(R.id.btnSignin)
        confirmPass = findViewById(R.id.etConfirmPass)
        auth = Firebase.auth
    }
}