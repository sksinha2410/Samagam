package com.ingray.samagam

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
const val TOPIC = "mess"
class FCMNotification : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var title:EditText
    private lateinit var message:EditText
    private lateinit var token:EditText
    private lateinit var send:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fcmnotification)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        title = findViewById(R.id.title)
        message = findViewById(R.id.message)
        token = findViewById(R.id.tokens)
        send = findViewById(R.id.send)
        send.setOnClickListener{
            val title = title.text.toString()
            val message = message.text.toString()
            if(title.isNotEmpty()&&message.isNotEmpty()){
                PushNotification(
                    NotificationData("Hi","Hello"),
                    TOPIC
                ).also {
                    sendNotification(it)
                }


            }
        }
    }

    private fun sendNotification(notification:PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
//                Log.d(TAG,"Response:${Gson().toJson(response)}")
            }else{
                Log.e(TAG,response.errorBody().toString())
            }
        }catch (e:Exception){
            Log.e(TAG,e.toString())
        }
    }
}