package com.ingray.samagam


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.DataClass.Events

class AddAlumniBatchDialog(private val context: Context,private val clubName:String) {
private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Clubs")

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.add_alumni_custom_dialog, null)
        val confirm = dialogView.findViewById<View>(R.id.confirm)
        val cancel = dialogView.findViewById<View>(R.id.cancel)
        val etBatch = dialogView.findViewById<TextView>(R.id.etBatch)

        // Set model data to the views
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        confirm.setOnClickListener{
            val batch = etBatch.text.toString()
            if (batch.length != 9){
                Toast.makeText(context, "Invalid Batch", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                dbRef.child(clubName).child("OfficeBearer").child("Members").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val members = snapshot.value
                            dbRef.child(clubName).child("Alumni").child(batch).child("Members").setValue(members)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        snapshot.ref.removeValue()
                                        alertDialog.dismiss()
                                        Toast.makeText(context, "Alumni Batch Added", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            var map = HashMap<String, Any>()
                            map.put("batch", batch)
                            dbRef.child(clubName).child("Alumni").child(batch).updateChildren(map)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                    }
                })
            }
        }

        cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}