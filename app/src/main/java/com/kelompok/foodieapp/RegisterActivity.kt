package com.kelompok.foodieapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Database
        db = DatabaseHelper(this)

        // Tombol Daftar
        binding.btnDaftar.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi input kosong
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi panjang password
            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Password minimal 6 karakter!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Simpan ke database
            if (db.registerUser(name, email, password)) {
                Toast.makeText(
                    this,
                    "Akun berhasil dibuat, silakan login",
                    Toast.LENGTH_SHORT
                ).show()

                // Kembali ke Login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Email sudah terdaftar!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Teks untuk pindah ke halaman Login
        binding.tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}