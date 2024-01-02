package com.ingray.samagam

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    //Initalizing the variables
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var signup:TextView
    private lateinit var signin:Button
    private lateinit var auth: FirebaseAuth
    lateinit var sEmail:String
    lateinit var sPass:String
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        callVariablesById()
        callOnClickListeners()

    }

    private fun callOnClickListeners() {
        signup.setOnClickListener{
            var intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)

            checkTheConditions()

            signin.setOnClickListener{
                sEmail = email.text.toString()
                sPass = password.text.toString()
                auth.signInWithEmailAndPassword(sEmail, sPass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            if(auth.currentUser?.isEmailVerified == true){
                                Log.d(TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
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
    }

    private fun checkTheConditions() {
        if (TextUtils.isEmpty(email.text.toString())) {
            Toast.makeText(
                applicationContext,
                "Please enter email!!",
                Toast.LENGTH_LONG
            )
                .show()
            return
        }

        if (TextUtils.isEmpty(password.text.toString())) {
            Toast.makeText(
                applicationContext,
                "Please enter password!!",
                Toast.LENGTH_LONG
            )
                .show()
            return
        }
    }

    private fun callVariablesById() {
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPass)
        signup = findViewById(R.id.redSignup)
        signin= findViewById(R.id.btnSignin)

        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
//            reload()
        }
    }

}
