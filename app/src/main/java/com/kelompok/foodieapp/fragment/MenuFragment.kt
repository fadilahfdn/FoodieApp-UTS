package com.kelompok.foodieapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelompok.foodieapp.R
import com.kelompok.foodieapp.FoodDetailActivity
import com.kelompok.foodieapp.adapter.MenuAdapter
import com.kelompok.foodieapp.data.MenuItem
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MenuAdapter
    private lateinit var db: DatabaseHelper
    private var allMenus = listOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        adapter = MenuAdapter(emptyList()) { menuItem ->
            val intent = Intent(requireContext(), FoodDetailActivity::class.java).apply {
                putExtra("MENU_ID",    menuItem.id)
                putExtra("MENU_NAME",  menuItem.name)
                putExtra("MENU_DESC",  menuItem.description)
                putExtra("MENU_PRICE", menuItem.price)
                putExtra("MENU_CATEGORY", menuItem.category)
                putExtra("MENU_IMAGE", menuItem.imageRes)
            }
            startActivity(intent)
        }

        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenu.adapter = adapter

        loadMenus()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = allMenus.filter {
                    it.name.contains(newText ?: "", ignoreCase = true)
                }
                adapter.updateData(filtered)
                return true
            }
        })
    }

    private fun loadMenus() {
        val rawMenus = db.getAllMenus()
        allMenus = rawMenus.map {
            val imageResName = it["image_url"] as? String ?: ""
            val resId = if (imageResName.isNotEmpty()) {
                requireContext().resources.getIdentifier(imageResName, "drawable", requireContext().packageName)
            } else 0

            MenuItem(
                id          = it["id"] as Int,
                name        = it["name"] as String,
                description = it["description"] as String,
                price       = it["price"] as Int,
                category    = it["category"] as String,
                imageRes    = if (resId != 0) resId else R.drawable.ic_launcher_background,
                rating      = it["rating"] as Double
            )
        }
        adapter.updateData(allMenus)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}