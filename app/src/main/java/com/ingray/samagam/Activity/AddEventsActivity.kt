package com.ingray.samagam.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.DataClass.Events
import com.ingray.samagam.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEventsActivity : AppCompatActivity() {
    private lateinit var event_name: EditText
    private lateinit var reg_link: EditText
    private lateinit var event_type: EditText
    private lateinit var brochure_link: EditText
    private lateinit var start_time: TextView
    private lateinit var end_time: TextView
    private lateinit var date: TextView
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

    private var startTime: Calendar = Calendar.getInstance()
    private var endTime: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_events)
        callById()
        ev= Events()
        callOnClick()
        getItemFromSpinner()



    }

    private fun callOnClick() {
        start_time.setOnClickListener{
            showTimePickerDialog(true)

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
                deRef.child("Clubs").child(selectedItem).child("Events").child(time).setValue(ev)
                deRef.child("Events").child(time).setValue(ev)
            }

        }
    }

    private fun checkInit():Boolean {
        if (TextUtils.isEmpty(event_name.text.toString())) {
            Toast.makeText(this, "Enter Event Name", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(reg_link.text.toString())) {
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

        ev.event_starttime=start_time.text.toString()
        ev.event_endtime=end_time.text.toString()
        ev.event_date=date.text.toString()
        ev.event_venue=venue.text.toString()
        ev.description=description.text.toString()

    }

    private fun getItemFromSpinner() {
        val data= arrayOf("SAC","Expresso","IEEE","Natvansh","ASME","Saptak","Think India","Vista","Total Chaos","E-Cell","GDSC","Hackslash","Tesla","DesCo","ASCE","Robotics","GYB","Incubation Centre","Others")
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
    }
}