package com.kelompok.foodieapp.data

data class FoodItem(
    val id: Int,
    val name: String,
    val desc: String,
    val price: Int,
    val imageRes: Int,
    val rating: Float = 0f,
    val badge: String? = null
)