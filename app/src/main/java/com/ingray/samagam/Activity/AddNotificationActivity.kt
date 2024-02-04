package com.ingray.samagam.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ingray.samagam.Constants
import com.ingray.samagam.DataClass.Notification
import com.ingray.samagam.R
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.UUID

class AddNotificationActivity : AppCompatActivity() {
    private lateinit var title:EditText
    private lateinit var message:EditText
    private lateinit var description:EditText
    private lateinit var submit:Button
    private lateinit var imageUrl:ImageView
    private lateinit var pdfUrl:ImageView
    private lateinit var progress: ProgressBar
    private lateinit var progresspdf: ProgressBar
    private lateinit var clubName:String
    private lateinit var clubUrl:String
    val Pick_image=1
    var purl:String=""
    var pdfurl:String=""
    var storageReference = FirebaseStorage.getInstance().reference
    private var dbRef: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("Notification")
    val storage = FirebaseStorage.getInstance()
    val storageRef: StorageReference = storage.reference.child("pdfs")

    private val openPdfChooser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedPdfUri = result.data?.data
                if (selectedPdfUri != null) {
                    progresspdf.visibility = View.VISIBLE
                    uploadPdf(selectedPdfUri)
                } else {
                    progresspdf.visibility = View.GONE
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Failed to get PDF file.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notification)
        callByID()
        progress.visibility = View.GONE
        progresspdf.visibility = View.GONE
        val itnt = intent
        clubName = itnt.getStringExtra("clubName")!!
        clubUrl = itnt.getStringExtra("clubUrl")!!

        imageUrl.setOnClickListener{
            openGallery()

        }
        pdfUrl.setOnClickListener{
            val pdfPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            pdfPickerIntent.type = "application/pdf"
            openPdfChooser.launch(pdfPickerIntent)

        }



        submit.setOnClickListener {
            var cTime = Calendar.getInstance().timeInMillis.toString()
            val title = title.text.toString()
            val message = message.text.toString()
            val description = description.text.toString()

            if(title =="" || message ==""){
                Toast.makeText(applicationContext,"Title or message can't be empty",Toast.LENGTH_SHORT).show()
            }else{
                sendNotification()
                val notification = Notification(title,message,purl,clubUrl,cTime,"",0,pdfurl,description)
                dbRef?.child(cTime)?.setValue(notification)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Notification Added", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
    private fun uploadPdf(pdfUri: Uri) {
        // Generate a unique filename for the PDF
        val pdfFileName = "${UUID.randomUUID()}.pdf"

        // Create a reference to the file in Firebase Storage
        val pdfRef = storageReference.child("pdfs/$pdfFileName")

        // Upload the PDF file
        pdfRef.putFile(pdfUri)
            .addOnSuccessListener {
                // Handle successful upload
                Toast.makeText(this, "File uploaded successfully!", Toast.LENGTH_SHORT).show()
                progresspdf.visibility = View.GONE
                pdfRef.downloadUrl.addOnSuccessListener { uri ->
                    pdfurl = uri.toString()
                }
            }
            .addOnFailureListener { exception ->
                progresspdf.visibility = View.GONE
                // Handle unsuccessful upload
                Toast.makeText(this, "Error uploading file: $exception", Toast.LENGTH_SHORT).show()
            }
        }

    private fun sendNotification() {
        val title = clubName+" : "+title.text.toString()
        val message = message.text.toString()

        // Check if title and message are not empty
        if (title.isNotEmpty() && message.isNotEmpty()) {
            // AsyncTask to send the notification in the background
            sendNotificationWithImage(title, message, purl)
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
                conn.setRequestProperty("Authorization", "key=${Constants.SERVER_KEY}")

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

            imageUrl.setImageURI(resultUri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun uploadImageToFirebase(imageUri: Uri) {
        val fileRef: StorageReference =
            storageReference.child("PosterEvent/" + Calendar.getInstance().timeInMillis.toString() + "profile.jpg")

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

                Glide.with(applicationContext).load(purl).into(imageUrl)
                progress.visibility = View.INVISIBLE
            }
        }.addOnFailureListener { // Handle the failure to upload
            Toast.makeText(applicationContext, "Failed.", Toast.LENGTH_LONG).show()
            progress.visibility = View.INVISIBLE
        }
    }


    private fun callByID() {
        title=findViewById(R.id.title)
        message=findViewById(R.id.message)
        description=findViewById(R.id.description)
        submit=findViewById(R.id.submit)
        imageUrl=findViewById(R.id.imageUrl)
        pdfUrl=findViewById(R.id.pdfUrl)
        progress = findViewById(R.id.sale_progressBar)
        progresspdf = findViewById(R.id.sale_progressBars)
    }
}