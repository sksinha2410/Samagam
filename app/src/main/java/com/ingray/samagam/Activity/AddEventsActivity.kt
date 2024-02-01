package com.ingray.samagam.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ingray.samagam.Constants
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.DataClass.Users
import com.ingray.samagam.R
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEventsActivity : AppCompatActivity() {
    private lateinit var event_name: EditText
    private lateinit var reg_link: EditText
    private lateinit var event_type: EditText
    private lateinit var brochure_link: EditText
    private lateinit var start_time: TextView
    private lateinit var end_time: TextView
    private lateinit var clubName: TextView
    private lateinit var date: TextView
    private lateinit var eventPoster: ImageView
    private lateinit var venue: EditText
    private lateinit var description: EditText
    private lateinit var btn_submit: Button
    private lateinit var spinner: Spinner
    private var deRef: DatabaseReference= FirebaseDatabase.getInstance().reference
    private lateinit var selectedItem:String
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var start:String
    private lateinit var end:String
    lateinit var ev:Events
    val Pick_image=1
    private lateinit var userType:String
    var storageReference = FirebaseStorage.getInstance().reference
    var purl:String=""
    private lateinit var progress:ProgressBar
    private var startTime: Calendar = Calendar.getInstance()
    private var endTime: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_events)
        val intent = intent
        userType = intent.getStringExtra("userType")!!
        callById()
        spinner.visibility = View.GONE
        clubName.visibility = View.GONE
        progress.visibility = View.INVISIBLE
        ev= Events()
        getItemFromSpinner()
        callOnClick()

        if (userType == "1"){
            spinner.visibility = View.VISIBLE
            clubName.visibility = View.GONE
        }else{
            spinner.visibility = View.GONE
            clubName.visibility = View.VISIBLE
            clubName.text = userType
            selectedItem = userType
        }


    }

    private fun callOnClick() {
        start_time.setOnClickListener{
            showTimePickerDialog(true)

        }
        eventPoster.setOnClickListener{
            openGallery()
        }
        end_time.setOnClickListener{
            showTimePickerDialog(false)

        }
        date.setOnClickListener{
            showDatePickerDialog()
        }
        btn_submit.setOnClickListener{

            var bool:Boolean=checkInit()
            if(bool){
                initEvents()

                val time=Calendar.getInstance().timeInMillis.toString()

                ev.key = time
                deRef.child("Clubs").child(selectedItem).child("Events").child(time).setValue(ev)
                deRef.child("Events").child(time).setValue(ev)
                Toast.makeText(applicationContext,"Event added",Toast.LENGTH_LONG).show()
                sendNotification()
                val intent = Intent(applicationContext,MainActivity::class.java)
                startActivity(intent)
                finish()
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

            eventPoster.setImageURI(resultUri)
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

                Glide.with(applicationContext).load(purl).into(eventPoster)
                progress.visibility = View.INVISIBLE
            }
        }.addOnFailureListener { // Handle the failure to upload
            Toast.makeText(applicationContext, "Failed.", Toast.LENGTH_LONG).show()
            progress.visibility = View.INVISIBLE
        }
    }

    private fun checkInit():Boolean {
        if (TextUtils.isEmpty(event_name.text.toString())) {
            Toast.makeText(this, "Enter Event Name", Toast.LENGTH_SHORT).show()
        }else if (purl .isEmpty()){
            Toast.makeText(this, "Please upload event poster", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(reg_link.text.toString())) {
            Toast.makeText(this, "Enter reg link", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(brochure_link.text.toString())) {
            Toast.makeText(this, "Enter brochure Link", Toast.LENGTH_SHORT)
                .show()
        } else if (TextUtils.isEmpty(event_type.text.toString())) {
            Toast.makeText(this, "Enter Event type", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(venue.text.toString())) {
            Toast.makeText(this, "Enter Venue", Toast.LENGTH_SHORT).show()
        } else if (start_time.text.toString().length!=5) {
            Toast.makeText(this, "Start Time not selected", Toast.LENGTH_SHORT).show()
        } else if (end_time.text.toString().length!=5) {
            Toast.makeText(this, "End Time not selected", Toast.LENGTH_SHORT).show()
        } else if (date.text.toString().length!=10) {
            Toast.makeText(
                this,
                "Date is not selected",
                Toast.LENGTH_SHORT
            ).show()

        }else{
            return true;
        }
        return false
    }

    private fun initEvents() {

        ev.event_name=event_name.text.toString()
        ev.reg_link=reg_link.text.toString()
        ev.event_type=event_type.text.toString()
        ev.brochure_link=brochure_link.text.toString()
        ev.club_name = selectedItem
        ev.purl = purl
        ev.event_starttime=start_time.text.toString()
        ev.event_endtime=end_time.text.toString()
        ev.event_date=date.text.toString()
        ev.event_venue=venue.text.toString()
        ev.description=description.text.toString()
        ev.notified = "0"

        val resultInMillis = convertDateTimeToMillis(date.text.toString(), start_time.text.toString())

        ev.event_date_time = resultInMillis



    }
    fun convertDateTimeToMillis(eventDate: String, eventTime: String): Long {
        val dateTimeString = "$eventDate $eventTime"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(dateTimeString) ?: Date(0)

        // Set milliseconds to zero
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }
    private fun sendNotification() {
        val title = selectedItem+":"+event_name.text.toString()
        val dateOrig = date.text.toString()
        val dup = dateOrig.substring(8,10)+dateOrig.substring(4,8)+dateOrig.substring(0,4)
        val message = "Event scheduled on: "+dup+"from "+start_time.text.toString()

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

    private fun getItemFromSpinner() {
        val data= arrayOf("SAC","Expresso","IEEE","Natvansh","ASME","Saptak","Think India","Vista","Total Chaos","E-Cell","GDSC","Hackslash","Tesla","DesCo","ASCE","Robotics","GYB","Incubation Centre","Others","Chess","NSS","Sankalp","SAE")
        val adapt=ArrayAdapter(this,android.R.layout.simple_spinner_item,data)
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=adapt
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedItem = parent.getItemAtPosition(position).toString()
                // Handle the selected item
                Toast.makeText(applicationContext,selectedItem,Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle no selection
            }
        }
    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                date.text = getDateString(selectedDate)
                ev.event_date = date.text.toString()

            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun getDateString(calendar: Calendar): String {
        return inputDateFormat.format(calendar.time)
    }
    private fun showTimePickerDialog(isStartTime: Boolean) {
        val calendar = if (isStartTime) startTime else endTime
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)

                if (isStartTime) {
                    startTime = calendar
                    start_time.text = getTimeString(calendar)
                    start = getTimeString(calendar)
                    ev.event_starttime = start_time.text.toString()


                } else {
                    endTime = calendar
                    end_time.text = getTimeString(calendar)
                    end = getTimeString(calendar)
                    ev.event_endtime = end_time.text.toString()

                }
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun getTimeString(calendar: Calendar): String {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }

    private fun callById() {
        event_name=findViewById(R.id.event_name)
        reg_link=findViewById(R.id.reg_link)
        event_type=findViewById(R.id.event_type)
        brochure_link=findViewById(R.id.brochure_link)
        start_time=findViewById(R.id.start_time)
        end_time=findViewById(R.id.end_time)
        date=findViewById(R.id.date)
        venue=findViewById(R.id.venue)
        description=findViewById(R.id.description)
        btn_submit=findViewById(R.id.btn_submit)
        spinner=findViewById(R.id.spinner)
        clubName=findViewById(R.id.clubName)
        eventPoster=findViewById(R.id.event_poster)
        progress = findViewById(R.id.sale_progressBar)
    }
}