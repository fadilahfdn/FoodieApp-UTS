package com.kelompok.foodieapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.ActivityFoodDetailBinding

class FoodDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodDetailBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi DatabaseHelper bawaan A1
        db = DatabaseHelper(this)

        // Menerima data Intent yang dikirim dari Katalog
        val menuId    = intent.getIntExtra("MENU_ID", 0)
        val menuName  = intent.getStringExtra("MENU_NAME") ?: ""
        val menuDesc  = intent.getStringExtra("MENU_DESC") ?: ""
        val menuPrice = intent.getIntExtra("MENU_PRICE", 0)

        // Set data ke komponen UI XML milik UTS kalian
        binding.tvDetailName.text  = menuName
        binding.tvDetailDesc.text  = menuDesc
        binding.tvDetailPrice.text = "Rp ${String.format("%,d", menuPrice).replace(',', '.')}"

        // Fungsi klik tombol Pesan untuk menyimpan ke tabel cart SQLite
        binding.btnPesan.setOnClickListener {
            val success = db.addToCart(menuId, menuName, menuPrice)
            if (success) {
                Toast.makeText(this, "✅ $menuName ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                finish() // Kembali ke layar sebelumnya
            } else {
                Toast.makeText(this, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            }
        }
    }
}