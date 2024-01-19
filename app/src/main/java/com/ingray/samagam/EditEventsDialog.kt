package com.ingray.samagam


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ingray.samagam.DataClass.Events
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditEventsDialog(private val activity: AppCompatActivity,private val context: Context, private val model: Events,clubName:String) {
    var clubName = clubName
    private lateinit var purl:String
    val Pick_image=1
    private lateinit var event_date:TextView
    private lateinit var start_time:TextView
    private lateinit var end_time:TextView
    private lateinit var poster:ImageView
    var storageReference = FirebaseStorage.getInstance().reference
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var startTime: Calendar = Calendar.getInstance()
    private var endTime: Calendar = Calendar.getInstance()


    @SuppressLint("SetTextI18n", "MissingInflatedId")
    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_edit_events, null)

        // Access dialog views and set model data
        val eventName = dialogView.findViewById<TextView>(R.id.event_name)
        val eventType = dialogView.findViewById<TextView>(R.id.event_type)
        poster = dialogView.findViewById<ImageView>(R.id.event_poster)
        val description = dialogView.findViewById<TextView>(R.id.description)
        val regLink = dialogView.findViewById<TextView>(R.id.reg_link)
        val brochureLink = dialogView.findViewById<TextView>(R.id.brochure_link)
         start_time= dialogView.findViewById<TextView>(R.id.start_time)
         end_time= dialogView.findViewById<TextView>(R.id.end_time)
         event_date= dialogView.findViewById<TextView>(R.id.date)
        val venue = dialogView.findViewById<TextView>(R.id.venue)
        val btn_save = dialogView.findViewById<TextView>(R.id.btn_save)
        val btn_cancel = dialogView.findViewById<TextView>(R.id.btn_cancel)

        // Set model data to the views
        eventName.text = model.event_name
        eventType.text = model.event_type
        event_date.text = model.event_date
        start_time.text = model.event_starttime
        end_time.text= model.event_endtime
        description.text = model.description
        regLink.text = model.reg_link
        brochureLink.text = model.brochure_link
        venue.text = model.event_venue
        Glide.with(dialogView.context).load(model.purl).into(poster)

        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        start_time.setOnClickListener{
            showTimePickerDialog(true)

        }
//        poster.setOnClickListener{
//            openGallery()
//        }
        end_time.setOnClickListener{
            showTimePickerDialog(false)

        }
        event_date.setOnClickListener{
            showDatePickerDialog()
        }

        btn_save.setOnClickListener{
            val map = HashMap<String,String>()
            map.put("event_name",eventName.text.toString())
//            map.put("purl",purl)
            map.put("reg_link",regLink.text.toString())
            map.put("description",description.text.toString())
            map.put("event_type",eventType.text.toString())
            map.put("brochure_link",brochureLink.text.toString())
            map.put("event_starttime",start_time.text.toString())
            map.put("event_endtime",end_time.text.toString())
            map.put("event_date",event_date.text.toString())
            map.put("event_venue",venue.text.toString())


            FirebaseDatabase.getInstance().reference.child("Clubs").child(clubName)
                .child("Events").child(model.key).updateChildren(map as Map<String, Any>)

            FirebaseDatabase.getInstance().reference
                .child("Events").child(model.key).updateChildren(map as Map<String, Any>)
            alertDialog.dismiss()
        }
        btn_cancel.setOnClickListener{

            alertDialog.dismiss()
        }
    }
//    private fun openGallery() {
//        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        activity.startActivityForResult(gallery, Pick_image)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.P)
//    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == Pick_image && resultCode == Activity.RESULT_OK && data != null) {
//            val resultUri: Uri = data.data!!
//            uploadImageToFirebase(resultUri)
//            poster.setImageURI(resultUri)
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.P)
//    private fun uploadImageToFirebase(imageUri: Uri) {
//        val fileRef: StorageReference =
//            storageReference.child("PosterEvent/" + Calendar.getInstance().timeInMillis.toString() + "profile.jpg")
//
//        // Load the image into a Bitmap
//        val bitmap: Bitmap
//        try {
//            // Assuming imageUri is a valid URI
//            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
//            bitmap = ImageDecoder.decodeBitmap(source)
//            // Use the bitmap here...
//        } catch (e: IOException) {
//            e.printStackTrace()
//            return
//        }
//
//// Compress the image with reduced quality (adjust quality as needed)
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(
//            Bitmap.CompressFormat.JPEG,
//            60,
//            baos
//        ) // Adjust the quality here (50 in this example)
//
//        // Convert the compressed Bitmap to bytes
//        val data = baos.toByteArray()
//
//        // Upload the compressed image to Firebase Storage
//        val uploadTask = fileRef.putBytes(data)
//        uploadTask.addOnSuccessListener { // Handle the successful upload
//            fileRef.downloadUrl.addOnSuccessListener { uri ->
//                purl = uri.toString()
//
//                Glide.with(context).load(purl).into(poster)
//
//            }
//        }.addOnFailureListener { // Handle the failure to upload
//            Toast.makeText(this.context, "Failed.", Toast.LENGTH_LONG).show()
//
//        }
//    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this.context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                event_date.text = getDateString(selectedDate)

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
            this.context,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)

                if (isStartTime) {
                    startTime = calendar
                    start_time.text = getTimeString(calendar)



                } else {
                    endTime = calendar
                    end_time.text = getTimeString(calendar)


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
}