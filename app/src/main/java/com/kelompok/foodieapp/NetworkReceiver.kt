package com.kelompok.foodieapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkReceiver : BroadcastReceiver() {

    var onNetworkChange: ((Boolean) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        onNetworkChange?.invoke(isInternetAvailable(context))
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val cm           = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network      = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}