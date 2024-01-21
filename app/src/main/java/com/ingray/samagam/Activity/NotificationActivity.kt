package com.ingray.samagam.Activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.ingray.samagam.Constants.Companion.SERVER_KEY
import com.ingray.samagam.R
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class NotificationActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSendNotification: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        editTextTitle = findViewById(R.id.title)
        editTextMessage = findViewById(R.id.message)
        buttonSendNotification = findViewById(R.id.button)

        // Subscribe to the topic only once
        FirebaseMessaging.getInstance().subscribeToTopic("All")
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/All")
        FirebaseMessaging.getInstance().subscribeToTopic("topics/All")

        buttonSendNotification.setOnClickListener {
            sendNotification()
        }
    }

    private fun sendNotification() {
        val title = editTextTitle.text.toString()
        val message = editTextMessage.text.toString()

        // Check if title and message are not empty
        if (title.isNotEmpty() && message.isNotEmpty()) {
            // AsyncTask to send the notification in the background
            sendNotificationWithImage(title, message, "https://firebasestorage.googleapis.com/v0/b/samagam-e10a2.appspot.com/o/Tesla.png?alt=media&token=d0c7bfc1-5786-4691-805f-1503ec385b8b")
        } else {
            // Display an error message if title or message is empty
            Toast.makeText(this, "Title and message cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNotificationWithImage(title: String, message: String, imageUrl: String) {
        val title = title
        val message = message
        val imageUrl = imageUrl

        // AsyncTask to send the notification in the background
        SendNotificationTask().execute(title, message, imageUrl)
    }

    private inner class SendNotificationTask : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg params: String): Boolean {
            val title = params[0]
            val message = params[1]
            val imageUrl = params[2]

            return try {
                // FCM server URL
                val url = URL("https://fcm.googleapis.com/fcm/send")

                // Create a connection
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Authorization", "key=$SERVER_KEY")

                // Enable input/output streams
                conn.doOutput = true

                // Create JSON payload
                val jsonPayload = JSONObject().apply {
                    put("to", "/topics/All")
                    val data = JSONObject().apply {
                        put("title", title)
                        put("message", message)
                        put("imageUrl", imageUrl)
                    }
                    put("data", data)
                }

                // Log statements for debugging
                Log.d("SendNotificationTask", "Notification payload: $jsonPayload")

                // Write JSON payload to the connection's output stream
                val os = conn.outputStream
                os.write(jsonPayload.toString().toByteArray(Charsets.UTF_8))
                os.close()

                // Get the response code
                val responseCode: Int = conn.responseCode

                // Log statements for debugging
                Log.d("SendNotificationTask", "Response code: $responseCode")

                // Close the connection
                conn.disconnect()

                // Check if the notification was sent successfully (HTTP status code 200)
                responseCode == 200
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SendNotificationTask", "Error sending notification: ${e.message}")
                false
            }
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                Toast.makeText(applicationContext, "Notification sent successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Failed to send notification", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
