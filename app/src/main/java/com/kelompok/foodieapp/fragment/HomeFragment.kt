package com.kelompok.foodieapp.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelompok.foodieapp.R
import com.kelompok.foodieapp.adapter.GridMenuAdapter
import com.kelompok.foodieapp.data.ApiService
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.FragmentHomeBinding
import java.util.concurrent.Executors

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var gridAdapter: GridMenuAdapter
    private lateinit var db: DatabaseHelper

    private var isDataLoaded = false
    private var isLoading = false // Penjaga agar tidak spam API saat scroll
    private var currentCategory: String? = null

    private val displayedIds = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        db = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("session", 0)
        binding.tvGreeting.text = "Halo, ${prefs.getString("user_name", "Pengguna")}! 👋"

        gridAdapter = GridMenuAdapter(mutableListOf())
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvRecommendation.layoutManager = layoutManager
        binding.rvRecommendation.adapter = gridAdapter

        // Tombol Kategori di atas
        binding.btnCatRandom.setOnClickListener { fetchRecommendations(null, true) }
        binding.btnCatChicken.setOnClickListener { fetchRecommendations("Chicken", true) }
        binding.btnCatBeef.setOnClickListener { fetchRecommendations("Beef", true) }
        binding.btnCatSeafood.setOnClickListener { fetchRecommendations("Seafood", true) }
        binding.btnCatDesert.setOnClickListener { fetchRecommendations("Dessert", true) }
        binding.btnCatPasta.setOnClickListener { fetchRecommendations("Pasta", true) }
        binding.btnCatVegetarian.setOnClickListener { fetchRecommendations("Vegetarian", true) }

        // Deteksi Infinite Scroll
        binding.rvRecommendation.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Jika dy > 0, artinya user sedang scroll ke bawah
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading) {
                        // Jika posisi scroll sudah mencapai item terakhir
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            // Ambil data baru untuk disambung ke bawah (isReset = false)
                            fetchRecommendations(currentCategory, false)
                        }
                    }
                }
            }
        })

        if (!isDataLoaded) {
            fetchRecommendations(null, true)
        }
    }

    /**
     * @param category: Kategori API. Jika null, akan acak.
     * @param isReset: True jika mengganti kategori (hapus list). False jika infinite scroll (tambah di bawah).
     */
    private fun fetchRecommendations(category: String?, isReset: Boolean) {
        currentCategory = category
        isLoading = true
        binding.progressBar.visibility = View.VISIBLE
        binding.tvApiStatus.text = if (isReset) "Mengambil data..." else "Memuat lebih banyak..."

        Executors.newSingleThreadExecutor().execute {
            // Ambil data lokal (hanya saat reset/pertama kali)
            val localMeals = if (isReset) {
                db.getMenusByCategory(category).map {
                    mapOf(
                        "id" to it["id"].toString(),
                        "name" to it["name"].toString(),
                        "image_url" to it["image_url"].toString(),
                        "description" to it["description"].toString(),
                        "price" to it["price"].toString(),
                        "category" to it["category"].toString()
                    )
                }
            } else emptyList()

            // Ambil data API
            val apiMeals = ApiService.getMenusByCategory(category)
            val combinedMeals = localMeals + apiMeals

            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                isLoading = false
                binding.progressBar.visibility = View.GONE

                if (combinedMeals.isNotEmpty()) {
                    isDataLoaded = true
                    binding.tvApiStatus.text = "Menampilkan kategori: ${category ?: "Acak"}"

                    if (isReset) {
                        displayedIds.clear()
                        displayedIds.addAll(combinedMeals.map{it["id"] ?: ""})
                        gridAdapter.resetData(combinedMeals)
                    } else {
                        val uniqueMeals = combinedMeals.filter { !displayedIds.contains(it["id"]) }
                        if (uniqueMeals.isNotEmpty()) {
                            // Rekam ID baru dan tambahkan ke layar
                            displayedIds.addAll(uniqueMeals.map { it["id"] ?: "" })
                            gridAdapter.appendData(uniqueMeals)
                        } else {
                            // Kalau sudah habis/semua duplikat, tampilkan pesan tamat
                            binding.tvApiStatus.text = "🏁 Semua menu sudah ditampilkan!"
                        }
                    }
                } else {
                    binding.tvApiStatus.text = "⚠️ Selesai / Gagal memuat"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}