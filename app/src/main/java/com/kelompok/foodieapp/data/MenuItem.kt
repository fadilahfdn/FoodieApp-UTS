package com.kelompok.foodieapp.data

data class MenuItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val rating: Double = 4.5
)