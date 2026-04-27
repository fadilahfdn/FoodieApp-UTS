package com.kelompok.foodieapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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
            showSuccessSnackbar("Berhasil ditambahkan ke keranjang!")
        }
    }

    private fun showSuccessSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT)

        val customView = LayoutInflater.from(this)
            .inflate(R.layout.snackbar_success, null)
        customView.findViewById<TextView>(R.id.tvMessage).text = message

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
        snackbarLayout.addView(customView, 0)

        snackbar.show()
    }
}