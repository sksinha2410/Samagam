package com.ingray.samagam.Activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ingray.samagam.CustomDialog
import com.ingray.samagam.Fragments.Fragment_Home
import com.ingray.samagam.Fragments.FramentFeed
import com.ingray.samagam.Fragments.LiveEventsFragment
import com.ingray.samagam.Fragments.ProfileFragment
import com.ingray.samagam.R

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav:BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController:NavController
    private lateinit var toolbar:Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        bottomNav = findViewById(R.id.bottom_nav_view)
        navController = findNavController(R.id.fragmentContainerView)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.fragment_Home,R.id.framentFeed,R.id.liveEventsFragment,R.id.profileFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)
        bottomNav.setupWithNavController(navController)
    }
}