package com.kelompok.foodieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kelompok.foodieapp.databinding.ItemOrderHistoryBinding

class OrderHistoryAdapter(
    private var items: List<Map<String, Any>>
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvOrderName.text  = item["menu_name"] as String
            tvOrderDate.text  = item["order_date"] as String
            tvOrderQty.text   = "x${item["quantity"]}"
            tvOrderPrice.text = "Rp ${
                String.format("%,d", item["total_price"] as Int).replace(',', '.')
            }"
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Map<String, Any>>) {
        items = newItems
        notifyDataSetChanged()
    }
}