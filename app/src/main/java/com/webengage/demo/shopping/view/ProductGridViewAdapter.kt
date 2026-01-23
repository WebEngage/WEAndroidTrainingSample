package com.webengage.demo.shopping.view

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.webengage.demo.shopping.R

data class ProductGridViewItem(
    val imageUrl: String,
    val title: String,
    val price: String
)

class ProductGridViewAdapter(
    private val activity: Activity?,
    private val products: List<ProductGridViewItem>
) : RecyclerView.Adapter<ProductGridViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productContainer: FrameLayout = view.findViewById(R.id.product_container)
        val imageView: ImageView = view.findViewById(R.id.productIconImageView)
        val titleText: TextView = view.findViewById(R.id.productShortDescTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_child_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        
        holder.productContainer.visibility = View.VISIBLE
        holder.titleText.text = product.title
        
        Glide.with(holder.imageView.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.headphone)
            .error(R.drawable.headphone)
            .into(holder.imageView)
    }

    override fun getItemCount() = products.size
}