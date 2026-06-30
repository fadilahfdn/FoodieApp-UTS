package com.kelompok.foodieapp

import com.kelompok.foodieapp.data.FoodItem
import com.kelompok.foodieapp.data.FoodRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodieAppUnitTest {

    @Test
    fun testFoodItemCreation() {
        val foodItem = FoodItem(
            id = 99,
            name = "Nasi Pecel",
            desc = "Nasi pecel pincuk khas Madiun pedas manis",
            price = 15000,
            category = "Makanan",
            imageRes = 0,
            rating = 4.8f,
            badge = "Baru"
        )

        assertEquals(99, foodItem.id)
        assertEquals("Nasi Pecel", foodItem.name)
        assertEquals(15000, foodItem.price)
        assertEquals("Baru", foodItem.badge)
    }

    @Test
    fun testFoodRepositoryList() {
        val list = FoodRepository.getMenu()
        assertEquals(6, list.size)
        
        val firstItem = list[0]
        assertEquals("Nasi Goreng Spesial", firstItem.name)
        assertEquals(25000, firstItem.price)
    }

    @Test
    fun testPriceFormattingLogic() {
        val price = 25000
        val formatted = "Rp ${String.format("%,d", price).replace(',', '.')}"
        assertEquals("Rp 25.000", formatted)
    }

    @Test
    fun testHaversineDistanceCalculation() {
        // Koordinat Toko UPNVJ (Pusat)
        val lat1 = -6.34625
        val lon1 = 106.80415
        
        // Koordinat Toko Kedai Margonda
        val lat2 = -6.37311
        val lon2 = 106.83332

        val r = 6371000.0 // Jari-jari bumi dalam meter
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distanceInMeters = r * c
        val distanceInKm = distanceInMeters / 1000

        // Jarak antara UPNVJ dan Margonda berkisar 4.4 km
        assertTrue(distanceInKm > 4.0)
        assertTrue(distanceInKm < 5.0)
    }
}
