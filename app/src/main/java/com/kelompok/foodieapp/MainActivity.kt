package com.kelompok.foodieapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kelompok.foodieapp.databinding.ActivityMainBinding
import com.kelompok.foodieapp.fragment.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Lokasi berhasil diaktifkan!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Izin lokasi ditolak, fitur jarak tidak akan maksimal.", Toast.LENGTH_LONG).show()
        }
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Jika user menekan "Allow/Izinkan"
            } else {
                // Jika user menekan "Don't Allow/Tolak"
                Toast.makeText(this, "Notifikasi dimatikan. Anda mungkin melewatkan info pesanan.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

        // Cek sesi loginz
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

        askLocationPermission()
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
        ContextCompat.registerReceiver(
            this,
            networkReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
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

    private fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Jika belum ada izin, langsung tembak pop-up
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun askNotificationPermission() {
        // Izin ini hanya diwajibkan oleh Google untuk Android 13 (Tiramisu / API 33) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Izin sudah pernah diberikan sebelumnya, tidak perlu memunculkan pop-up lagi
            } else {
                // Sistem belum diberi izin, munculkan Pop-up sekarang!
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}