package com.ingray.samagam.Activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ingray.samagam.Fragments.Fragment_Home
import com.ingray.samagam.Fragments.FramentFeed
import com.ingray.samagam.Fragments.LiveEventsFragment
import com.ingray.samagam.Fragments.ProfileFragment
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.homeScreen -> {
                replaceFragment(Fragment_Home()) // Replace with your FragmentA instance
                true
            }
            R.id.events -> {
                replaceFragment(LiveEventsFragment()) // Replace with your FragmentB instance
                true
            }
            R.id.feed -> {
                replaceFragment(FramentFeed()) // Replace with your FragmentB instance
                true
            }
            R.id.profile-> {
                replaceFragment(ProfileFragment()) // Replace with your FragmentB instance
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHostFragment, fragment) // Replace R.id.fragment_container with your container ID
            .addToBackStack(null) // Add to back stack if needed
            .commit()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}