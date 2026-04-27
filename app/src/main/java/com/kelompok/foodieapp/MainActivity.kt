package com.kelompok.foodieapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kelompok.foodieapp.databinding.ActivityMainBinding
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation button to MenuListActivity (will work once Anggota 5 adds it)
        binding.btnLihatMenu.setOnClickListener {
            startActivity(Intent(this, MenuListActivity::class.java))
        }
    }
}