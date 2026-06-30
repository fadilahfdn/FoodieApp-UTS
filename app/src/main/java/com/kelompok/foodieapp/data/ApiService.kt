package com.kelompok.foodieapp.data



object ApiService {

    private const val BASE_URL = "https://router.project-osrm.org/route/v1"

    fun getRouteDistance(userLat: Double, userLng: Double, tokoLat: Double, tokoLng: Double): Double? {
        val client = okhttp3.OkHttpClient()
        val url = "https://router.project-osrm.org/route/v1/driving/$userLng,$userLat;$tokoLng,$tokoLat?overview=false"

        // 1. Intip URL yang ditembak
        android.util.Log.d("OSRM_DEBUG", "Menembak URL: $url")

        return try {
            val request = okhttp3.Request.Builder()
                .url(url)
                .addHeader("User-Agent", "FoodieApp/1.0")
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return null

            // 2. Intip respons mentah dari server OSRM
            android.util.Log.d("OSRM_DEBUG", "Respons Mentah Server: $body")

            val json = org.json.JSONObject(body)
            val code = json.optString("code")

            if (code != "Ok") {
                android.util.Log.e("OSRM_DEBUG", "OSRM Menolak Rute! Kode Error: $code")
                return null
            }

            val routes = json.optJSONArray("routes")
            if (routes != null && routes.length() > 0) {
                val route = routes.getJSONObject(0)
                route.getDouble("distance")
            } else {
                null
            }
        } catch (e: Exception) {
            // 3. Intip kalau ada crash internet atau parsing JSON
            android.util.Log.e("OSRM_DEBUG", "Terjadi Crash/Error: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}