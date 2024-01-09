package com.ingray.samagam.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.util.Calendar

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_events)
        callById()

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
        val ev:Events= Events()
        ev.event_name=event_name.getText().toString()
        ev.reg_link=reg_link.getText().toString()
        ev.event_type=event_type.getText().toString()
        ev.brochure_link=brochure_link.getText().toString()
        ev.event_starttime=start_time.getText().toString()
        ev.event_endtime=end_time.getText().toString()
        ev.event_date=date.getText().toString()
        ev.event_venue=venue.getText().toString()
        ev.description=description.getText().toString()

        btn_submit.setOnClickListener{
            val time=Calendar.getInstance().timeInMillis.toString()
            deRef.child("Clubs").child(selectedItem).child("Events").child(time).setValue(ev)
            deRef.child("Events").child(time).setValue(ev)
        }
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