package com.ingray.samagam

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
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
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPass)
        signup = findViewById(R.id.redSignup)
        signin= findViewById(R.id.btnSignin)

        auth = Firebase.auth
        signup.setOnClickListener{
            var intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }

        signin.setOnClickListener{
            sEmail = email.text.toString()
            sPass = password.text.toString()
            auth.signInWithEmailAndPassword(sEmail, sPass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
//                    updateUI(user)
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
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
//            reload()
        }
    }

}
