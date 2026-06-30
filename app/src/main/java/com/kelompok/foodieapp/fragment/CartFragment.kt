package com.kelompok.foodieapp.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelompok.foodieapp.NotificationHelper
import com.kelompok.foodieapp.adapter.CartAdapter
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        adapter = CartAdapter(
            items      = emptyList(),
            onIncrease = { cartId, currentQty ->
                db.updateCartQuantity(cartId, currentQty + 1)
                loadCart()
            },
            onDecrease = { cartId, currentQty ->
                if (currentQty > 1) {
                    db.updateCartQuantity(cartId, currentQty - 1)
                } else {
                    db.removeFromCart(cartId)
                }
                loadCart()
            },
            onDelete = { cartId ->
                db.removeFromCart(cartId)
                loadCart()
            }
        )

        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = adapter

        loadCart()

        binding.btnCheckout.setOnClickListener {
            val email = requireActivity()
                .getSharedPreferences("session", 0)
                .getString("user_email", "") ?: ""

            val items = db.getCartItems()

            if (items.isEmpty()) return@setOnClickListener

            val totalItems = items.sumOf { it["quantity"] as Int }

            db.saveOrderHistory(items)
            db.addTotalOrdered(email, totalItems)
            db.clearCart()

            Toast.makeText(requireContext(), "Memproses pesanan Anda...", Toast.LENGTH_SHORT).show()

            //Langsung kosongkan UI keranjang di layar
            loadCart()

            // Picu Notifikasi dengan jeda 3 deti
            val appContext = requireActivity().applicationContext
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                NotificationHelper.showCheckoutNotification(appContext)
            }, 3000)
        }
    }

    private fun loadCart() {
        val items = db.getCartItems()
        val total = db.getCartTotal()

        if (items.isEmpty()) {
            binding.tvCartEmpty.visibility   = View.VISIBLE
            binding.rvCart.visibility        = View.GONE
            binding.layoutCartFooter.visibility = View.GONE
        } else {
            binding.tvCartEmpty.visibility   = View.GONE
            binding.rvCart.visibility        = View.VISIBLE
            binding.layoutCartFooter.visibility = View.VISIBLE
            adapter.updateData(items)
            binding.tvCartTotal.text = "Total: Rp ${String.format("%,d", total).replace(',', '.')}"
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}