package com.kelompok.foodieapp.data

import com.kelompok.foodieapp.R

object FoodRepository {
    fun getMenu(): List<FoodItem> {
        return listOf(
            FoodItem(
                id = 1,
                name = "Nasi Goreng Spesial",
                desc = "Nasi goreng porsi besar dengan telur dan ayam pilihan",
                price = 25000,
                imageRes = R.drawable.menu_nasgor,
                rating = 4.9f,
                badge = "Terlaris"
            ),
            FoodItem(
                id = 2,
                name = "Ayam Taliwang",
                desc = "Ayam bakar khas Lombok dengan bumbu rempah pedas gurih",
                price = 32000,
                imageRes = R.drawable.menu_ayam_taliwang,
                rating = 4.8f,
                badge = "Rekomendasi"
            ),
            FoodItem(
                id = 3,
                name = "Mie Bakso",
                desc = "Mie kuah hangat dengan bakso sapi kenyal dan pelengkap",
                price = 20000,
                imageRes = R.drawable.menu_mie_bakso,
                rating = 4.7f,
                badge = null
            ),
            FoodItem(
                id = 4,
                name = "Sate Maranggi",
                desc = "Sate daging sapi khas Purwakarta dengan bumbu kecap manis",
                price = 28000,
                imageRes = R.drawable.menu_sate_maranggi,
                rating = 4.6f,
                badge = "Baru"
            ),
            FoodItem(
                id = 5,
                name = "Es Jeruk Segar",
                desc = "Minuman jeruk peras segar dingin, cocok menemani makan",
                price = 8000,
                originalPrice = 12000,
                imageRes = R.drawable.menu_es_jeruk,
                rating = 4.5f,
                badge = "Diskon"
            ),
            FoodItem(
                id = 6,
                name = "Es Teh Manis",
                desc = "Teh manis dingin klasik, menyegarkan dan selalu pas",
                price = 6000,
                imageRes = R.drawable.menu_es_teh,
                rating = 4.4f,
                badge = null
            )
        )
    }
}