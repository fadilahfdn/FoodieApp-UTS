package com.kelompok.foodieapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kelompok.foodieapp.data.ApiService
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.ActivityFoodDetailBinding
import java.util.concurrent.Executors

class FoodDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodDetailBinding
    private lateinit var db: DatabaseHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        val menuId    = intent.getIntExtra("MENU_ID", 0)
        val menuName  = intent.getStringExtra("MENU_NAME") ?: ""
        val menuDesc  = intent.getStringExtra("MENU_DESC") ?: ""
        val menuPrice = intent.getIntExtra("MENU_PRICE", 0)
        val menuCategory = intent.getStringExtra("MENU_CATEGORY") ?: "Umum"

        // Handle image based on type (String for API, Int for Local)
        val imageExtra = intent.extras?.get("MENU_IMAGE")
        val menuImagePublic = imageExtra as? String
        val menuImage = imageExtra as? Int ?: 0

        binding.tvDetailName.text  = menuName
        binding.tvDetailCategory.text = menuCategory
        binding.tvDetailDesc.text  = menuDesc
        binding.tvDetailPrice.text = "Rp ${String.format("%,d", menuPrice).replace(',', '.')}"
        if (!menuImagePublic.isNullOrEmpty()) {
            // Jika dari API, pakai Glide
            Glide.with(this)
                .load(menuImagePublic)
                .placeholder(R.drawable.loading_images)
                .error(R.drawable.ic_launcher_background)
                .into(binding.imgDetail)
        } else if (menuImage != 0) {
            // Jika dari SQLite, pakai resource lokal
            binding.imgDetail.setImageResource(menuImage)
        } else {
            // Fallback kalau dua-duanya kosong
            binding.imgDetail.setImageResource(R.drawable.ic_launcher_background)
        }

        binding.tvDetailRating.text = "4.5"

        binding.btnPesan.setOnClickListener {
            if (menuId != 0 && menuName.isNotEmpty()) {
                val success = db.addToCart(menuId, menuName, menuPrice)
                if (success) {
                    Toast.makeText(this, "✅ $menuName ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Data menu tidak valid!", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val storeData = db.getStoreByMenuId(menuId)
        if (storeData != null) {
            val namaToko = storeData["nama_toko"] as String
            val tokoLat = storeData["latitude"] as Double
            val tokoLng = storeData["longitude"] as Double

            binding.tvStoreName.text = "$namaToko"

            // 3. Lakukan pengecekan lokasi secara senyap (Silent Check)
            checkLocationAndCalculate(tokoLat, tokoLng)
        } else {
            binding.tvStoreName.text = "Tidak Diketahui"
            binding.tvDistance.text = "-- km"
        }
    }

    private fun checkLocationAndCalculate(tokoLat: Double, tokoLng: Double) {
        // Cek diam-diam, apakah MainActivity tadi sudah berhasil dapat izin?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            // Izin ada! Langsung ambil koordinat GPS
            @SuppressWarnings("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLat = location.latitude
                    val userLng = location.longitude

                    // Tembak API OSRM di Background Thread (agar UI tidak macet)
                    Executors.newSingleThreadExecutor().execute {
                        val distanceInMeters =
                            ApiService.getRouteDistance(userLat, userLng, tokoLat, tokoLng)

                        runOnUiThread {
                            if (distanceInMeters != null) {
                                val distanceInKm = distanceInMeters / 1000
                                binding.tvDistance.text =
                                    "${String.format("%.1f", distanceInKm)} km"
                            } else {
                                binding.tvDistance.text = "Gagal Diketahui"
                            }
                        }
                    }
                } else {
                    binding.tvDistance.text = "GPS Dimatikan"
                }
            }
        } else {
            // Jika user menolak izin di awal, tampilkan teks fallback saja
            binding.tvDistance.text = "Tidak diketahui"
        }
    }
}