package com.kelompok.foodieapp.data

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.min // Pastikan ini ter-import jika minOf error

object ApiService {

    private val client = OkHttpClient()
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    fun getMenusByCategory(category: String? = null): List<Map<String, String>> {
        val result = mutableListOf<Map<String, String>>()
        return try {
            val categories = listOf("Chicken", "Beef", "Seafood", "Dessert", "Pasta", "Vegetarian")
            val selectedCategory = category ?: categories.random()

            val url      = "${BASE_URL}filter.php?c=$selectedCategory"
            val request  = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body     = response.body?.string() ?: return result
            val json     = JSONObject(body)
            val meals    = json.optJSONArray("meals") ?: return result

            for (i in 0 until minOf(meals.length(), 16)) {
                val meal = meals.getJSONObject(i)

                val randomPrices = listOf(20000, 25000, 30000, 35000, 40000, 45000, 50000)
                val price = randomPrices.random()

                val mockDescription = "Menu rekomendasi spesial hari ini dari dapur kami. Disiapkan dengan bahan pilihan yang pastinya menggugah selera!"

                result.add(mapOf(
                    "id"        to meal.getString("idMeal"),
                    "name"      to meal.getString("strMeal"),
                    "image_url" to meal.getString("strMealThumb"),
                    "description" to mockDescription,
                    "price" to price.toString(),
                    "category" to selectedCategory
                ))
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            result
        }
    }
}