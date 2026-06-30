package com.kelompok.foodieapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kelompok.foodieapp.databinding.ActivityMainBinding
import com.kelompok.foodieapp.fragment.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek sesi login
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        if (!prefs.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        loadFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home    -> loadFragment(HomeFragment())
                R.id.nav_menu    -> loadFragment(MenuFragment())
                R.id.nav_cart    -> loadFragment(CartFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private lateinit var networkReceiver: NetworkReceiver

    override fun onStart() {
        super.onStart()
        networkReceiver = NetworkReceiver()
        networkReceiver.onNetworkChange = { isConnected ->
            runOnUiThread {
                binding.tvNetworkBanner.visibility =
                    if (!isConnected) View.VISIBLE else View.GONE
            }
        }
        val filter = android.content.IntentFilter(
            android.net.ConnectivityManager.CONNECTIVITY_ACTION
        )
        @Suppress("DEPRECATION")
        registerReceiver(networkReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkReceiver)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}