package com.webengage.demo.shopping.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webengage.demo.shopping.R
import com.bumptech.glide.Glide

class CarouselAdapter(private val items: List<CarouselItem>) : RecyclerView.Adapter<CarouselAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.carouselTitle)
        val imageView: ImageView = view.findViewById(R.id.carouselImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Log.d("CarouselAdapter", "Binding item $position: title=${item.title}, imageUrl=${item.imageUrl}")
        
        holder.titleText.text = item.title
        
        // Clear any previous image first
        holder.imageView.setImageDrawable(null)
        
        Glide.with(holder.imageView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.headphone)
            .error(R.drawable.headphone)
            .skipMemoryCache(true)
            .into(holder.imageView)
    }

    override fun getItemCount() = items.size
}