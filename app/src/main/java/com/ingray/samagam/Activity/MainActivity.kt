package com.ingray.samagam.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ingray.samagam.Adapters.ClubsAdapter
import com.ingray.samagam.Fragments.Fragment_Home
import com.ingray.samagam.R

class MainActivity : AppCompatActivity() {

    private val itemList: MutableList<Any> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstFragment = Fragment_Home()
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHostFragment, firstFragment)
            .commit()

        // Example of replacing fragments on some event or button click
        // replaceFragment(SecondFragment()) // Call this to replace with another fragment
    }

    // Function to replace fragments
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHostFragment, fragment)
            .addToBackStack(null) // This allows the user to navigate back
            .commit()
    }
}