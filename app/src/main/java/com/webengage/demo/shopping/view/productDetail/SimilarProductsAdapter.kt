package com.webengage.demo.shopping.view.productDetail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.webengage.demo.shopping.R

data class SimilarProduct(
    val image: String,
    val title: String,
    val price: String,
    val deeplink: String
)

class SimilarProductsAdapter(
    private val items: List<SimilarProduct>,
    private val onItemClick: (SimilarProduct) -> Unit
) : RecyclerView.Adapter<SimilarProductsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.similarProductImage)
        val titleText: TextView = view.findViewById(R.id.similarProductTitle)
        val priceText: TextView = view.findViewById(R.id.similarProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_similar_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.titleText.text = item.title
        holder.priceText.text = item.price

        Glide.with(holder.imageView.context)
            .load(item.image)
            .placeholder(R.drawable.headphone)
            .error(R.drawable.headphone)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = items.size
}
