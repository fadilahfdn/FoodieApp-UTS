package com.kelompok.foodieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kelompok.foodieapp.databinding.ItemCartRowBinding

class CartAdapter(
    private var items: List<Map<String, Any>>,
    private val onIncrease: (cartId: Int, currentQty: Int) -> Unit,
    private val onDecrease: (cartId: Int, currentQty: Int) -> Unit,
    private val onDelete: (cartId: Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        val cartId   = item["id"] as Int
        val name     = item["menu_name"] as String
        val price    = item["menu_price"] as Int
        val quantity = item["quantity"] as Int

        with(holder.binding) {
            tvCartItemName.text  = name
            tvCartItemPrice.text = "Rp ${String.format("%,d", price * quantity).replace(',', '.')}"
            tvQuantity.text      = quantity.toString()

            btnIncrease.setOnClickListener { onIncrease(cartId, quantity) }
            btnDecrease.setOnClickListener { onDecrease(cartId, quantity) }
            btnDelete.setOnClickListener   { onDelete(cartId) }
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Map<String, Any>>) {
        items = newItems
        notifyDataSetChanged()
    }
}