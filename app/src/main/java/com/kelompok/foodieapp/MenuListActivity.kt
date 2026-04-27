package com.kelompok.foodieapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kelompok.foodieapp.data.FoodRepository
import com.kelompok.foodieapp.databinding.ActivityMenuListBinding

class MenuListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val menuList = FoodRepository.getMenu()

        // Ganti ArrayAdapter dengan FoodAdapter custom
        val adapter = FoodAdapter(this, menuList)
        binding.lvMenu.adapter = adapter

        binding.lvMenu.setOnItemClickListener { _, _, position, _ ->
            val selectedFood = menuList[position]
            val intent = Intent(this, FoodDetailActivity::class.java)
            intent.putExtra("FOOD_ID", selectedFood.id)
            startActivity(intent)
        }
    }
}