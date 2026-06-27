package com.kelompok.foodieapp.data

data class MenuItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val imageRes: Int,
    val rating: Double = 4.5
)