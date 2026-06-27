package com.kelompok.foodieapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.ActivityFoodDetailBinding

class FoodDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodDetailBinding
    private lateinit var db: DatabaseHelper

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
        val menuImagePublic = if (imageExtra is String) imageExtra else null
        val menuImage = if (imageExtra is Int) imageExtra else 0

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
    }
}