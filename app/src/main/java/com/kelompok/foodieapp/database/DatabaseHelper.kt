package com.kelompok.foodieapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "foodieapp.db", null, 6) { // Upgrade to version 6

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Tabel Toko
        val createTableToko = """
            CREATE TABLE toko (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama_toko TEXT,
                latitude REAL,
                longitude REAL
            )
        """.trimIndent()

        // Dummy Data Toko
        db?.execSQL(createTableToko)
        db.execSQL("""
            INSERT INTO toko (nama_toko, latitude, longitude) 
            VALUES ('Warung Pusat FoodieApp', -6.34625, 106.80415)
        """)

        // 2. Tabel Relasi (Many-to-Many: Satu menu bisa dijual di banyak toko)
        val createTableTokoMenu = """
            CREATE TABLE toko_menu (
                toko_id INTEGER,
                menu_id INTEGER,
                FOREIGN KEY(toko_id) REFERENCES toko(id),
                FOREIGN KEY(menu_id) REFERENCES menus(id)
            )
        """.trimIndent()
        db?.execSQL(createTableTokoMenu)

        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                total_ordered INTEGER DEFAULT 0
            )
        """)
        db.execSQL("""
            CREATE TABLE menus (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                price INTEGER NOT NULL,
                category TEXT,
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

        db.execSQL("""
            CREATE TABLE order_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                menu_name TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                total_price INTEGER NOT NULL,
                order_date TEXT NOT NULL
             )
        """)
        insertDefaultMenus(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS menus")
        db.execSQL("DROP TABLE IF EXISTS cart")
        db.execSQL("DROP TABLE IF EXISTS order_history")
        onCreate(db)
    }

    private fun insertDefaultMenus(db: SQLiteDatabase) {
        data class MenuData(val name: String, val desc: String, val price: Int, val category: String, val img: String)
        val menus = listOf(
            MenuData("Nasi Goreng Spesial", "Porsi besar dengan telur dan ayam pilihan", 25000, "Nasi" , "menu_nasgor"),
            MenuData("Ayam Taliwang", "Ayam bakar khas Lombok dengan bumbu rempah pedas", 32000, "Ayam" , "menu_ayam_taliwang"),
            MenuData("Mie Bakso", "Mie kuah hangat dengan bakso sapi kenyal dan pelengkap", 20000, "Sapi" , "menu_mie_bakso"),
            MenuData("Sate Maranggi", "Sate daging sapi khas Purwakarta dengan bumbu kecap manis", 28000, "Sapi" , "menu_sate_maranggi"),
            MenuData("Es Jeruk Segar", "Minuman jeruk peras segar dingin, cocok menemani makan", 8000, "Minuman",  "menu_es_jeruk"),
            MenuData("Es Teh Manis", "Teh manis dingin klasik, menyegarkan dan selalu pas", 6000, "Minuman",  "menu_es_teh")
        )
        menus.forEach { menu ->
            val cv = ContentValues().apply {
                put("name", menu.name)
                put("description", menu.desc)
                put("price", menu.price)
                put("category", menu.category)
                put("image_url", menu.img)
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
        
        val idCol = cursor.getColumnIndex("id")
        val nameCol = cursor.getColumnIndex("name")
        val descCol = cursor.getColumnIndex("description")
        val priceCol = cursor.getColumnIndex("price")
        val catCol = cursor.getColumnIndex("category")
        val imgCol = cursor.getColumnIndex("image_url")
        val rateCol = cursor.getColumnIndex("rating")

        while (cursor.moveToNext()) {
            val item = mutableMapOf<String, Any>()
            if (idCol != -1) item["id"] = cursor.getInt(idCol)
            if (nameCol != -1) item["name"] = cursor.getString(nameCol)
            if (descCol != -1) item["description"] = cursor.getString(descCol)
            if (priceCol != -1) item["price"] = cursor.getInt(priceCol)
            if (catCol != -1) item["category"] = cursor.getString(catCol) ?: ""
            if (imgCol != -1) item["image_url"] = cursor.getString(imgCol) ?: ""
            if (rateCol != -1) item["rating"] = cursor.getDouble(rateCol)
            result.add(item)
        }
        cursor.close()
        return result
    }

    fun getMenusByCategory(category: String?): List<Map<String, Any>> {
        val result = mutableListOf<Map<String, Any>>()
        val query = if (category == null) {
            "SELECT * FROM menus"
        } else {
            "SELECT * FROM menus WHERE category = ?"
        }
        val args = if (category == null) null else arrayOf(category)
        
        val cursor = readableDatabase.rawQuery(query, args)
        
        val idCol = cursor.getColumnIndex("id")
        val nameCol = cursor.getColumnIndex("name")
        val descCol = cursor.getColumnIndex("description")
        val priceCol = cursor.getColumnIndex("price")
        val catCol = cursor.getColumnIndex("category")
        val imgCol = cursor.getColumnIndex("image_url")
        val rateCol = cursor.getColumnIndex("rating")

        while (cursor.moveToNext()) {
            val item = mutableMapOf<String, Any>()
            if (idCol != -1) item["id"] = cursor.getInt(idCol)
            if (nameCol != -1) item["name"] = cursor.getString(nameCol)
            if (descCol != -1) item["description"] = cursor.getString(descCol)
            if (priceCol != -1) item["price"] = cursor.getInt(priceCol)
            if (catCol != -1) item["category"] = cursor.getString(catCol) ?: ""
            if (imgCol != -1) item["image_url"] = cursor.getString(imgCol) ?: ""
            if (rateCol != -1) item["rating"] = cursor.getDouble(rateCol)
            result.add(item)
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

    fun updateCartQuantity(cartId: Int, newQuantity: Int) {
        val cv = ContentValues().apply { put("quantity", newQuantity) }
        writableDatabase.update("cart", cv, "id=?", arrayOf(cartId.toString()))
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

    // menghitung total item yang pernah dipesan
    fun getTotalItemsOrdered(email: String): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT total_ordered FROM users WHERE email=?", arrayOf(email)
        )
        val total = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return total
    }

    // menambah total pesanan
    fun addTotalOrdered(email: String, amount: Int): Boolean {
        return try {
            val cursor = readableDatabase.rawQuery(
                "SELECT total_ordered FROM users WHERE email=?", arrayOf(email)
            )
            if (cursor.moveToFirst()) {
                val current = cursor.getInt(0)
                cursor.close()
                val cv = ContentValues().apply { put("total_ordered", current + amount) }
                writableDatabase.update("users", cv, "email=?", arrayOf(email))
            } else {
                cursor.close()
            }
            true
        } catch (e: Exception) { false }
    }

    fun saveOrderHistory(items: List<Map<String, Any>>) {
        val date = java.text.SimpleDateFormat(
            "dd MMM yyyy, HH:mm", java.util.Locale("id", "ID")
        ).format(java.util.Date())

        items.forEach { item ->
            val cv = ContentValues().apply {
                put("menu_name",   item["menu_name"] as String)
                put("quantity",    item["quantity"] as Int)
                put("total_price", (item["menu_price"] as Int) * (item["quantity"] as Int))
                put("order_date",  date)
            }
            writableDatabase.insert("order_history", null, cv)
        }
    }

    fun getOrderHistory(): List<Map<String, Any>> {
        val result = mutableListOf<Map<String, Any>>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM order_history ORDER BY id DESC", null
        )
        while (cursor.moveToNext()) {
            result.add(mapOf(
                "id"          to cursor.getInt(0),
                "menu_name"   to cursor.getString(1),
                "quantity"    to cursor.getInt(2),
                "total_price" to cursor.getInt(3),
                "order_date"  to cursor.getString(4)
            ))
        }
        cursor.close()
        return result
    }

    // update profile
    fun updateUser(email: String, newName: String): Boolean {
        return try {
            val cv = ContentValues().apply { put("name", newName) }
            writableDatabase.update("users", cv, "email=?", arrayOf(email))
            true
        } catch (e: Exception) { false }
    }
}