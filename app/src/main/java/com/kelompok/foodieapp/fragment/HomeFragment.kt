package com.kelompok.foodieapp.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kelompok.foodieapp.R
import com.kelompok.foodieapp.adapter.GridMenuAdapter
import com.kelompok.foodieapp.data.FoodRepository
import com.kelompok.foodieapp.databinding.FragmentHomeBinding

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

        // 1. Sapaan User
        val prefs = requireActivity().getSharedPreferences("session", 0)
        binding.tvGreeting.text = "Halo, ${prefs.getString("user_name", "Pengguna")}! 👋"
        binding.btnPesanSekarang.setOnClickListener {
            val bottomNav = activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)

            // Picu perpindahan tab ke MenuFragment secara otomatis
            // PASTIKAN R.id.navigation_menu di bawah ini sama dengan ID menu di file res/menu/bottom_nav_menu.xml kelompokmu!
            bottomNav?.selectedItemId = R.id.nav_menu
        }

        // 2. Setup RecyclerView (Grid 2 Kolom)
        gridAdapter = GridMenuAdapter(mutableListOf())
        binding.rvRecommendation.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvRecommendation.isNestedScrollingEnabled = false
        binding.rvRecommendation.adapter = gridAdapter

        // Kembalikan fungsi teks "Lihat Semua" ke semula jika diperlukan
        binding.tvLihatSemua.text = "Lihat Semua"
        binding.tvLihatSemua.setOnClickListener {
            val bottomNav = activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)

            // Picu perpindahan tab ke MenuFragment secara otomatis
            // PASTIKAN R.id.navigation_menu di bawah ini sama dengan ID menu di file res/menu/bottom_nav_menu.xml kelompokmu!
            bottomNav?.selectedItemId = R.id.nav_menu
        }

        // 3. LOGIKA REFRESH SAAT DI-SCROLL KE ATAS / TARIK KE BAWAH
        binding.swipeRefresh.setOnRefreshListener {
            loadRandomMenus()

            // Matikan animasi loading berputar jika data selesai diacak
            binding.swipeRefresh.isRefreshing = false
        }

        // 4. Muat data pertama kali saat halaman dibuka
        loadRandomMenus()
    }

    private fun loadRandomMenus() {
        val randomMenus = FoodRepository.getMenu().shuffled().take(4)

        val mappedMenus = randomMenus.map { food ->
            val imageName = requireContext().resources.getResourceEntryName(food.imageRes)

            mapOf(
                "id"          to food.id.toString(),
                "name"        to food.name,
                "description" to food.desc,
                "price"       to food.price.toString(),
                "category"    to food.category,
                "image_url"   to imageName
            )
        }

        gridAdapter.resetData(mappedMenus)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}