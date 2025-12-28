package com.webengage.demo.shopping.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webengage.demo.shopping.R
import com.webengage.sdk.android.WebEngage
import com.webengage.personalization.WEPersonalization
import com.webengage.personalization.callbacks.WEPlaceholderCallback
import com.webengage.personalization.data.WECampaignData
import org.json.JSONArray
import org.json.JSONObject

class InlineFragment : Fragment(), WEPlaceholderCallback {

    private val weAnalytics = WebEngage.get().analytics()
    private lateinit var containerLL: LinearLayout

    override fun onStart() {
        super.onStart()
        weAnalytics.screenNavigated("screen1")
        WEPersonalization.get().registerWEPlaceholderCallback("S1C2", this)
        WEPersonalization.get().registerWEPlaceholderCallback("S1C3", this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerLL = view.findViewById(R.id.container)
    }

    private fun createCarouselView(carouselItems: List<ProductGridViewItem>) {
        val layoutInflater = LayoutInflater.from(activity)
        val categoryView = layoutInflater.inflate(
            R.layout.layout_product_list, null, false
        )
        containerLL.addView(categoryView, 0) // Add at top
        categoryView.id = View.generateViewId()
        
        val title = categoryView.findViewById<TextView>(R.id.tv_title)
        title.text = "Product Grid View"
        
        val recyclerView = categoryView.findViewById<RecyclerView>(R.id.rv_product_list)
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.isNestedScrollingEnabled = false
        
        val adapter = ProductGridViewAdapter(activity, carouselItems)
        recyclerView.adapter = adapter
    }

    private fun createCarouselComponent(carouselItems: List<CarouselItem>) {
        val layoutInflater = LayoutInflater.from(activity)
        val categoryView = layoutInflater.inflate(
            R.layout.layout_product_list, null, false
        )
        containerLL.addView(categoryView, 1)
        categoryView.id = View.generateViewId()
        
        val title = categoryView.findViewById<TextView>(R.id.tv_title)
        title.text = "Carousel"
        
        val recyclerView = categoryView.findViewById<RecyclerView>(R.id.rv_product_list)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        
        val adapter = CarouselAdapter(carouselItems)
        recyclerView.adapter = adapter
    }



    override fun onDataReceived(data: WECampaignData) {
        Log.d("WebEngage", "onDataReceived: ${data.targetViewId}")
        val payload = data.content?.customData
        
        when (data.targetViewId) {
            "S1C3" -> handleCarouselData(payload)
            "S1C2" -> handleProductGridData(payload)
        }
    }
    
    private fun handleCarouselData(payload: Map<String, Any>?) {
        payload?.get("productGrid")?.let { productData ->
            try {
                val categoryObject = JSONObject(productData as String)
                val products = categoryObject.getJSONArray("products")
                val carouselItems = mutableListOf<CarouselItem>()

                for (i in 0 until products.length()) {
                    val product = products.getJSONObject(i)
                    carouselItems.add(
                        CarouselItem(
                            "$i",
                            product.getString("title"),
                            product.getString("image")
                        )
                    )
                }

                activity?.runOnUiThread {
                    createCarouselComponent(carouselItems)
                }
            } catch (e: Exception) {
                Log.e("WebEngage", "Error parsing carousel data: ${e.message}")
            }
        }
    }
    
    private fun handleProductGridData(payload: Map<String, Any>?) {
        payload?.get("carousel")?.let { carouselData ->
            try {
                val carouselArray = JSONArray(carouselData as String)
                val carouselItems = mutableListOf<ProductGridViewItem>()

                for (i in 0 until carouselArray.length()) {
                    val category = carouselArray.getJSONObject(i)
                    val products = category.getJSONArray("products")
                    
                    for (j in 0 until products.length()) {
                        val product = products.getJSONObject(j)
                        carouselItems.add(
                            ProductGridViewItem(
                                product.getString("image"),
                                product.getString("title"),
                                product.getString("price")
                            )
                        )
                    }
                }

                activity?.runOnUiThread {
                    createCarouselView(carouselItems)
                }
            } catch (e: Exception) {
                Log.e("WebEngage", "Error parsing product grid data: ${e.message}")
            }
        }
    }

    override fun onPlaceholderException(
        campaignId: String?,
        targetViewId: String,
        error: Exception
    ) {
        Log.e("WebEngage", "Placeholder exception for $targetViewId: ${error.message}")
    }

    override fun onRendered(data: WECampaignData) {
        Log.d("WebEngage", "Rendered: ${data.targetViewId}")
    }
}

data class CarouselItem(val id: String, val title: String, val imageUrl: String)