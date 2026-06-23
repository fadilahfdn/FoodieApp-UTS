package com.kelompok.foodieapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "foodieapp.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE menus (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                price INTEGER NOT NULL,
                image_url TEXT,
                rating REAL DEFAULT 4.5
            )
        """)
        db.execSQL("""
            CREATE TABLE cart (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                menu_id INTEGER NOT NULL,
                menu_name TEXT NOT NULL,
                menu_price INTEGER NOT NULL,
                quantity INTEGER DEFAULT 1
            )
        """)
        insertDefaultMenus(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS menus")
        db.execSQL("DROP TABLE IF EXISTS cart")
        onCreate(db)
    }

    private fun insertDefaultMenus(db: SQLiteDatabase) {
        val menus = listOf(
            Triple("Nasi Goreng Spesial", "Porsi besar dengan telur dan ayam pilihan", 25000),
            Triple("Ayam Taliwang", "Ayam bakar khas Lombok dengan bumbu rempah pedas", 32000),
            Triple("Mie Bakso", "Mie kuah hangat dengan bakso sapi kenyal dan pelengkap", 20000),
            Triple("Sate Maranggi", "Sate daging sapi khas Purwakarta dengan bumbu kecap manis", 28000),
            Triple("Es Jeruk Segar", "Minuman jeruk peras segar dingin, cocok menemani makan", 8000),
            Triple("Es Teh Manis", "Teh manis dingin klasik, menyegarkan dan selalu pas", 6000)
        )
        menus.forEach { (name, description, price) ->
            val cv = ContentValues().apply {
                put("name", name)
                put("description", description)
                put("price", price)
            }
            db.insert("menus", null, cv)
        }
    }

    // ── USER METHODS ──────────────────────────────────────
    fun registerUser(name: String, email: String, password: String): Boolean {
        return try {
            val cv = ContentValues().apply {
                put("name", name); put("email", email); put("password", password)
            }
            writableDatabase.insert("users", null, cv)
            true
        } catch (e: Exception) { false }
    }

    fun loginUser(email: String, password: String): Map<String, String>? {
        val cursor = readableDatabase.rawQuery(
            "SELECT id, name, email FROM users WHERE email=? AND password=?",
            arrayOf(email, password)
        )
        return if (cursor.moveToFirst()) {
            mapOf(
                "id"    to cursor.getString(0),
                "name"  to cursor.getString(1),
                "email" to cursor.getString(2)
            ).also { cursor.close() }
        } else { cursor.close(); null }
    }

    // ── MENU METHODS ──────────────────────────────────────
    fun getAllMenus(): List<Map<String, Any>> {
        val result = mutableListOf<Map<String, Any>>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM menus", null)
        while (cursor.moveToNext()) {
            result.add(mapOf(
                "id"          to cursor.getInt(0),
                "name"        to cursor.getString(1),
                "description" to cursor.getString(2),
                "price"       to cursor.getInt(3),
                "image_url"   to (cursor.getString(4) ?: ""),
                "rating"      to cursor.getDouble(5)
            ))
        }
        cursor.close()
        return result
    }

    fun updateMenuImageUrl(menuId: Int, imageUrl: String) {
        val cv = ContentValues().apply { put("image_url", imageUrl) }
        writableDatabase.update("menus", cv, "id=?", arrayOf(menuId.toString()))
    }

    // ── CART METHODS ──────────────────────────────────────
    fun addToCart(menuId: Int, menuName: String, menuPrice: Int): Boolean {
        val existing = readableDatabase.rawQuery(
            "SELECT id, quantity FROM cart WHERE menu_id=?", arrayOf(menuId.toString())
        )
        return if (existing.moveToFirst()) {
            val newQty = existing.getInt(1) + 1
            val cv = ContentValues().apply { put("quantity", newQty) }
            writableDatabase.update("cart", cv, "menu_id=?", arrayOf(menuId.toString()))
            existing.close(); true
        } else {
            existing.close()
            val cv = ContentValues().apply {
                put("menu_id",    menuId)
                put("menu_name",  menuName)
                put("menu_price", menuPrice)
                put("quantity",   1)
            }
            writableDatabase.insert("cart", null, cv) > 0
        }
    }

    fun getCartItems(): List<Map<String, Any>> {
        val result = mutableListOf<Map<String, Any>>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM cart", null)
        while (cursor.moveToNext()) {
            result.add(mapOf(
                "id"         to cursor.getInt(0),
                "menu_id"    to cursor.getInt(1),
                "menu_name"  to cursor.getString(2),
                "menu_price" to cursor.getInt(3),
                "quantity"   to cursor.getInt(4)
            ))
        }
        cursor.close()
        return result
    }

    fun removeFromCart(cartId: Int) {
        writableDatabase.delete("cart", "id=?", arrayOf(cartId.toString()))
    }

    fun clearCart() {
        writableDatabase.delete("cart", null, null)
    }

    fun getCartTotal(): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT SUM(menu_price * quantity) FROM cart", null
        )
        val total = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return total
    }
}