package com.webengage.demo.shopping.view.home

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.webengage.demo.shopping.R
import com.webengage.personalization.WEInlineView
import com.webengage.personalization.callbacks.WEPlaceholderCallback
import com.webengage.personalization.data.WECampaignData
import java.lang.Exception

class ProductAdapter(
    private val activity: FragmentActivity?,
    private val productList: List<Product>,
    private val productClickListener: ProductClickListener?
) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, activity: Activity, product: Product) {
            val campaignContainer = itemView.findViewById<WEInlineView>(R.id.product_campaign)
            val productContainer = itemView.findViewById<FrameLayout>(R.id.product_container)

            // Bind your data to the views in the item layout
            if (product.type.isNotEmpty() && product.type == "campaign") {
                campaignContainer.visibility = View.VISIBLE
                productContainer.visibility = View.GONE
                campaignContainer.load(
                    "product_campaign_$position",
                    object : WEPlaceholderCallback {
                        override fun onDataReceived(data: WECampaignData) {
                            Log.d("TAG", "product adapter onDataReceived: ")
                        }

                        override fun onPlaceholderException(
                            campaignId: String?,
                            targetViewId: String,
                            error: Exception
                        ) {
                            Log.d("TAG", "product adapter onPlaceholderException: ")
                        }

                        override fun onRendered(data: WECampaignData) {
                            Log.d("TAG", "product adapter onRendered: ")
                        }

                    })
            } else {
                campaignContainer.visibility = View.GONE
                productContainer.visibility = View.VISIBLE
                val imageView = itemView.findViewById<ImageView>(R.id.productIconImageView)
                val resId =
                    activity.resources.getIdentifier(
                        product.image,
                        "drawable",
                        activity.packageName
                    )
                imageView.setImageResource(resId)
                val textView = itemView.findViewById<TextView>(R.id.productShortDescTextView)
                textView.text = product.title
                itemView.setOnClickListener {
                    productClickListener?.onProductClick(position, product)
                }
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