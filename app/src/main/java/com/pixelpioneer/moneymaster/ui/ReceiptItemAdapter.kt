package com.pixelpioneer.moneymaster.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.ReceiptItem

class ReceiptItemAdapter(
    private val items: MutableList<ReceiptItem>
) : RecyclerView.Adapter<ReceiptItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemPrice: TextView = view.findViewById(R.id.item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receipt, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemPrice.text = String.format("%.2f €", item.price)
    }

    override fun getItemCount() = items.size
}