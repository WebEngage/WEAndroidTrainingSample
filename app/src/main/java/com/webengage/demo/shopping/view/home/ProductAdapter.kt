package com.webengage.demo.shopping.view.home

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.webengage.demo.shopping.R

class ProductAdapter(private val activity: FragmentActivity?,
                     private val productList: List<Product>,
    private val productClickListener: ProductClickListener?) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, activity: Activity, product: Product) {
            // Bind your data to the views in the item layout
            val imageView = itemView.findViewById<ImageView>(R.id.productIconImageView)
            val resId = activity.resources.getIdentifier(product.image, "drawable", activity.packageName)
            imageView.setImageResource(resId)
            val textView = itemView.findViewById<TextView>(R.id.productShortDescTextView)
            textView.text = product.title
            itemView.setOnClickListener {
                productClickListener?.onProductClick(position, product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_child_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = productList[position]
        holder.bind(position, activity!!, item)
    }
}