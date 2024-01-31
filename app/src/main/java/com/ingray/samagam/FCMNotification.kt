package com.ingray.samagam.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.ingray.samagam.Constants.Companion.SERVER_KEY
import com.ingray.samagam.R
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FCMNotification : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSendNotification: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        editTextTitle = findViewById(R.id.title)
        editTextMessage = findViewById(R.id.message)
//        buttonSendNotification = findViewById(R.id.button)

        // Subscribe to the topic only once
        FirebaseMessaging.getInstance().subscribeToTopic("All")

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
            SendNotificationTask().execute(title, message)
        } else {
            // Display an error message if title or message is empty
            Toast.makeText(this, "Title and message cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class SendNotificationTask : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg params: String): Boolean {
            val title = params[0]
            val message = params[1]

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
                        put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/samagam-e10a2.appspot.com/o/Tesla.png?alt=media&token=d0c7bfc1-5786-4691-805f-1503ec385b8b")
                    }
                    put("data", data)
                }

                // Write JSON payload to the connection's output stream
                val os = conn.outputStream
                os.write(jsonPayload.toString().toByteArray(Charsets.UTF_8))
                os.close()

                // Get the response code
                val responseCode: Int = conn.responseCode

                // Close the connection
                conn.disconnect()

                // Check if the notification was sent successfully (HTTP status code 200)
                responseCode == 200
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                // Load and display the image in the notification
                val title = editTextTitle.text.toString()
                val message = editTextMessage.text.toString()
                loadImageFromUrl("https://firebasestorage.googleapis.com/v0/b/samagam-e10a2.appspot.com/o/Tesla.png?alt=media&token=d0c7bfc1-5786-4691-805f-1503ec385b8b", title, message)
            } else {
                Toast.makeText(applicationContext, "Failed to send notification", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadImageFromUrl(imageUrl: String, title: String, message: String) {
        // AsyncTask to load and display the image from the URL
        LoadImageTask(title, message).execute(imageUrl)
    }

    private inner class LoadImageTask(private val title: String, private val message: String) : AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg params: String): Bitmap? {
            val imageUrl = params[0]
            return try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                // Display the image in the notification
                showNotificationWithImage(result, title, message)
            } else {
                Toast.makeText(applicationContext, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNotificationWithImage(imageBitmap: Bitmap, title: String, message: String) {
        val channelId = "default_channel_id"
        val notificationId = 1

        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "Default Channel for Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification builder
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setLargeIcon(imageBitmap)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(imageBitmap))
            .setDefaults(Notification.DEFAULT_ALL) // Add default sound, vibration, and lights

        // Show the notification
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Handle permissions as needed
                return
            }
            notify(notificationId, builder.build())
        }
    }
}
