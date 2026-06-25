package com.kelompok.foodieapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnLogin.setOnClickListener {
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val user = db.loginUser(email, password)
            if (user != null) {
                val prefs = getSharedPreferences("session", MODE_PRIVATE)
                prefs.edit()
                    .putString("user_name", user["name"])
                    .putString("user_email", user["email"])
                    .putBoolean("is_logged_in", true)
                    .apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Email atau password salah!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}