package com.ingray.samagam.Activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
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
import com.ingray.samagam.R

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar
    private var permission_notif = false
    private lateinit var permissions: Array<String>
    private lateinit var notification: ImageView
    private lateinit var connectivityReceiver: BroadcastReceiver

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notification = findViewById(R.id.notification)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        }

        FirebaseMessaging.getInstance().subscribeToTopic("All")
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                var msg = "Done"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
            })

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNav = findViewById(R.id.bottom_nav_view)
        navController = findNavController(R.id.fragmentContainerView)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.fragment_Home, R.id.framentFeed, R.id.liveEventsFragment, R.id.profileFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        if (!permission_notif) {
            requestPermission()
        }

        notification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        // Initialize connectivity receiver
        connectivityReceiver = ConnectivityReceiver(this)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectivityReceiver)
    }

    private fun requestPermission() {
        try {
            if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                permission_notif = true
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    Toast.makeText(this, "Grant Permission for Latest Event Notification", Toast.LENGTH_SHORT).show()
                }
                requestPermissionLauncherNotification.launch(permissions[0])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val requestPermissionLauncherNotification = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        permission_notif = isGranted
        if (!isGranted) {
            showPermissionDialog("Notification Permission")
        }
    }

    private fun showPermissionDialog(permission_desc: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Alert for Notification Permission")
            setPositiveButton("Settings") { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
                startActivity(intent)
                dialog.dismiss()
            }
            setNegativeButton("Exit") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    // ConnectivityReceiver class to monitor internet connection
    private inner class ConnectivityReceiver(private val context: Context) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (!isConnected(context)) {
                showNoInternetDialog()
            }
        }

        private fun isConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        private fun showNoInternetDialog() {
            AlertDialog.Builder(context).apply {
                setTitle("No Internet Connection")
                setMessage("Please check your internet connection.")
                setPositiveButton("Retry") { dialog, _ ->
                    if (isConnected(context)) {
                        dialog.dismiss()
                    }
                }
                setCancelable(false)
                show()
            }
        }
    }
}