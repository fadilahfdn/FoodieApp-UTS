package com.kelompok.foodieapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kelompok.foodieapp.data.FoodRepository
import com.kelompok.foodieapp.databinding.ActivityFoodDetailBinding

class FoodDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val id = intent.getIntExtra("FOOD_ID", 0)
        val food = FoodRepository.getMenu().find { it.id == id }


        if (food != null) {
            binding.imgDetail.setImageResource(food.imageRes)
            binding.tvDetailName.text = food.name
            binding.tvDetailDesc.text = food.desc
            binding.tvDetailPrice.text = "Rp ${String.format("%,d", food.price).replace(',', '.')}"
            binding.tvDetailRating.text = food.rating.toString()
        }


        binding.btnPesan.setOnClickListener {
            Toast.makeText(this, "Berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke layar sebelumnya
        }
    }
}
