package com.kelompok.foodieapp

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.kelompok.foodieapp.data.FoodItem
import com.kelompok.foodieapp.databinding.ItemFoodRowBinding

class FoodAdapter(context: Context, private val items: List<FoodItem>) :
    ArrayAdapter<FoodItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ItemFoodRowBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ItemFoodRowBinding.bind(convertView)
        }

        val food = items[position]

        // Nama, deskripsi
        binding.tvFoodName.text = food.name
        binding.tvFoodDesc.text = food.desc

        // Harga format Rp 25.000
        binding.tvFoodPrice.text = "Rp ${String.format("%,d", food.price).replace(',', '.')}"

        // Gambar
        binding.imgFood.setImageResource(food.imageRes)

        // Rating
        binding.tvRating.text = food.rating.toString()

        // Badge
        if (food.badge != null) {
            binding.tvBadge.visibility = View.VISIBLE
            binding.tvBadge.text = food.badge
            val badgeColor = when (food.badge) {
                "Terlaris", "Rekomendasi" -> ContextCompat.getColor(context, R.color.brand_primary)
                "Baru" -> ContextCompat.getColor(context, R.color.badge_green)
                else -> ContextCompat.getColor(context, R.color.brand_primary)
            }
            binding.tvBadge.backgroundTintList =
                android.content.res.ColorStateList.valueOf(badgeColor)
        } else {
            binding.tvBadge.visibility = View.GONE
        }

        return binding.root
    }
}