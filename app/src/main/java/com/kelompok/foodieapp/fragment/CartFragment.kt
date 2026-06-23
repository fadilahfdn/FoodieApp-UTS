package com.kelompok.foodieapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        loadCart()

        binding.btnCheckout.setOnClickListener {
            db.clearCart()
            Toast.makeText(requireContext(), "Pesanan berhasil dikonfirmasi! 🎉", Toast.LENGTH_LONG).show()
            loadCart()
        }
    }

    private fun loadCart() {
        val items = db.getCartItems()
        val total = db.getCartTotal()

        if (items.isEmpty()) {
            binding.tvCartEmpty.visibility = View.VISIBLE
            binding.layoutCartContent.visibility = View.GONE
        } else {
            binding.tvCartEmpty.visibility = View.GONE
            binding.layoutCartContent.visibility = View.VISIBLE

            val summary = items.joinToString("\n") { item ->
                "• ${item["menu_name"]} x${item["quantity"]} — Rp ${
                    String.format("%,d", (item["menu_price"] as Int) * (item["quantity"] as Int)).replace(',', '.')
                }"
            }
            binding.tvCartItems.text = summary
            binding.tvCartTotal.text = "Total: Rp ${String.format("%,d", total).replace(',', '.')}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}