package com.webengage.demo.shopping

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartRecyclerAdapter(
    private var itemList: List<Product>,
    private val onButtonClickListener: (Product) -> Unit,
    private val context: Context,
) : RecyclerView.Adapter<CartRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.cartImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val button: Button = itemView.findViewById(R.id.removeCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        val resId = context.resources.getIdentifier(item.image, "drawable", context.packageName)
        holder.imageView.setImageResource(resId)
        holder.titleTextView.text = item.title
        holder.priceTextView.text = item.price
        holder.button.setOnClickListener{ onButtonClickListener(item)}
        Log.d("CartRecyclerAdapter", "onBindViewHolder " + item.title)
        // Set a click listener for the button
        //holder.button.setOnClickListener { onButtonClickListener(item) }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun refreshList(list: List<Product>) { Log.d("CartRecyclerAdapter", "refreshList list.size "+list)
        itemList = list
        notifyDataSetChanged()
    }
}