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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs    = requireActivity().getSharedPreferences("session", 0)
        val userName = prefs.getString("user_name", "Pengguna")
        binding.tvGreeting.text    = "Halo, $userName! 👋"
        binding.tvSubGreeting.text = "Mau makan apa hari ini?"

        // Setup grid 2 kolom
        gridAdapter = GridMenuAdapter(emptyList())
        binding.rvRecommendation.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvRecommendation.adapter = gridAdapter

        // Ambil data dari API di background thread
        binding.progressBar.visibility = View.VISIBLE
        Executors.newSingleThreadExecutor().execute {
            val meals = ApiService.getMenusByCategory()
            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                binding.progressBar.visibility = View.GONE
                if (meals.isNotEmpty()) {
                    binding.tvApiStatus.text = "${meals.size} rekomendasi dimuat dari web"
                    binding.tvApiStatus.setTextColor(
                        resources.getColor(R.color.brand_primary, null)
                    )
                    gridAdapter.updateData(meals)
                } else {
                    binding.tvApiStatus.text = "⚠️ Tidak dapat memuat rekomendasi (cek koneksi internet Anda)"
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}