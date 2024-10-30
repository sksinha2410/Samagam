package com.ingray.samagam

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class ConnectivityReceiver(private val context: Context) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!isConnected(context!!)) {
            showNoInternetDialog()
        }
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(context)
        val view = View.inflate(context, R.layout.connectivity_receiver, null)
        builder.setView(view)
        val dialog = builder.create()

        view.findViewById<Button>(R.id.btn_retry).setOnClickListener {
            if (isConnected(context)) {
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
