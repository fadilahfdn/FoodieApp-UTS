package com.kelompok.foodieapp.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kelompok.foodieapp.R
import com.kelompok.foodieapp.adapter.GridMenuAdapter
import com.kelompok.foodieapp.data.ApiService
import com.kelompok.foodieapp.databinding.FragmentHomeBinding
import java.util.concurrent.Executors

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var gridAdapter: GridMenuAdapter

    // Variabel penanda agar tidak fetch API berulang kali saat layar diputar/ditinggalkan
    private var isDataLoaded = false
    private var currentCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("session", 0)
        binding.tvGreeting.text = "Halo, ${prefs.getString("user_name", "Pengguna")}! 👋"

        gridAdapter = GridMenuAdapter(emptyList())
        binding.rvRecommendation.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvRecommendation.adapter = gridAdapter

        // Tombol Kategori
        binding.btnCatRandom.setOnClickListener { fetchRecommendations(null) }
        binding.btnCatChicken.setOnClickListener { fetchRecommendations("Chicken") }
        binding.btnCatBeef.setOnClickListener { fetchRecommendations("Beef") }
        binding.btnCatSeafood.setOnClickListener { fetchRecommendations("Seafood") }
        binding.btnCatVegetarian.setOnClickListener { fetchRecommendations("Vegetarian") }

        // Ambil data pertama kali jika belum pernah diambil
        if (!isDataLoaded) {
            fetchRecommendations(null)
        }
    }

    private fun fetchRecommendations(category: String?) {
        currentCategory = category
        binding.progressBar.visibility = View.VISIBLE
        binding.rvRecommendation.visibility = View.GONE
        binding.tvApiStatus.text = "Mengambil data..."

        Executors.newSingleThreadExecutor().execute {
            // Memanggil ApiService (pastikan kodenya sudah diubah jadi acak di pertanyaan sebelumnya)
            val meals = ApiService.getMenusByCategory(category)

            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                binding.progressBar.visibility = View.GONE
                binding.rvRecommendation.visibility = View.VISIBLE

                if (meals.isNotEmpty()) {
                    isDataLoaded = true
                    binding.tvApiStatus.text = "Kategori: ${category ?: "Acak"}"
                    binding.tvApiStatus.setTextColor(resources.getColor(R.color.brand_primary, null))
                    gridAdapter.updateData(meals)
                } else {
                    binding.tvApiStatus.text = "⚠️ Gagal memuat data (cek internet)"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}