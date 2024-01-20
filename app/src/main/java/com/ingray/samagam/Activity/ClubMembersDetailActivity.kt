package com.ingray.samagam.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ingray.samagam.R

class  ClubMembersDetailActivity : AppCompatActivity() {
    private lateinit var batchName: TextView
    private lateinit var members: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_members_detail)
        callbyId()
        members.itemAnimator=null

    }

    private fun callbyId() {
        batchName=findViewById(R.id.batchName)
        members=findViewById(R.id.members_recycler)
    }
}