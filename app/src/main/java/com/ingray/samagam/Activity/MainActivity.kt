package com.ingray.samagam.Activity


import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import android.Manifest;
import android.annotation.SuppressLint
import com.ingray.samagam.R
import android.provider.Settings
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ingray.samagam.Fragments.NotificationFragment
import com.ingray.samagam.Fragments.ProfileFragment


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav:BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController:NavController
    private lateinit var toolbar:Toolbar
    var permission_notif = false
    lateinit var permissions: Array<String>
    lateinit var notification:ImageView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notification = findViewById(R.id.notification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = arrayOf<String>(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        FirebaseMessaging.getInstance().subscribeToTopic("All")
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                var msg = "Done"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
            })
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/All")
        FirebaseMessaging.getInstance().subscribeToTopic("topics/All")
        toolbar = findViewById(R.id.toolbar)


        setSupportActionBar(toolbar)
        bottomNav = findViewById(R.id.bottom_nav_view)
        navController = findNavController(R.id.fragmentContainerView)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.fragment_Home,R.id.framentFeed,R.id.liveEventsFragment,R.id.profileFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)
        bottomNav.setupWithNavController(navController)
        if(!permission_notif){
            requestPermission();
        }
        notification.setOnClickListener{
            val intent  = Intent(this,NotificationActivity::class.java)
            startActivity(intent)
        }

    }


    private fun requestPermission() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    permissions.get(0)
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                permission_notif = true
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    Toast.makeText(
                        applicationContext,
                        "Grant Permission for Latest Event Notification ",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Grant Permission for Latest Event Notification ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                requestPermissionLauncherNotification.launch(permissions.get(0))
            }
        } catch (e: Exception) {
        }
    }

    private val requestPermissionLauncherNotification = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            permission_notif = true
        } else {
            permission_notif = false
            showPermissionDialog("Notification Permission")
        }
    }

    private fun showPermissionDialog(permission_desc: String) {
        AlertDialog.Builder(
            this@MainActivity
        ).setTitle("Alert for Notification Permission")
            .setPositiveButton("Settings", DialogInterface.OnClickListener { dialog, which ->
                val rintent = Intent()
                rintent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                rintent.setData(uri)
                startActivity(rintent)
                dialog.dismiss()
            }).setNegativeButton("Exit",
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            .show()
    }
}