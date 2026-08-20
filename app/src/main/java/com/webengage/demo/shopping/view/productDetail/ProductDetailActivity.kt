package com.webengage.demo.shopping.view.productDetail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.view.home.Product
import com.webengage.personalization.WEInlineView
import com.webengage.personalization.WEPersonalization
import com.webengage.personalization.callbacks.WEPlaceholderCallback
import com.webengage.personalization.data.WECampaignData
import com.webengage.sdk.android.WebEngage
import org.json.JSONObject

class ProductDetailActivity : AppCompatActivity(), WEPlaceholderCallback {

    companion object {
        private const val TAG = "ProductDetailActivity"
        private const val SCREEN_NAME = "ProductDetail"
        private const val SIMILAR_PRODUCTS_PROPERTY_ID = "PRODUCT_DETAIL_SIMILAR"
    }

    private lateinit var similarProductsContainer: LinearLayout
    private lateinit var similarProductsRecyclerView: RecyclerView
    private lateinit var bannerInlineView: WEInlineView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("product", Product::class.java)
        } else {
            intent.getSerializableExtra("product") as Product
        }

        val addToCartButton = findViewById<Button>(R.id.addToCartButton)
        addToCartButton.setOnClickListener { sendAddToCartResult(product) }

        similarProductsContainer = findViewById(R.id.similarProductsContainer)
        similarProductsRecyclerView = findViewById(R.id.similarProductsRecyclerView)
        bannerInlineView = findViewById(R.id.PRODUCT_DETAIL_BANNER)

        populateDetails(product)
    }

    override fun onStart() {
        super.onStart()

        // Navigate to screen so WebEngage can target campaigns to this screen
        WebEngage.get().analytics().screenNavigated(SCREEN_NAME)

        // Register callback for custom inline - Similar Products
        WEPersonalization.get().registerWEPlaceholderCallback(SIMILAR_PRODUCTS_PROPERTY_ID, this)
    }

    // --- WEPlaceholderCallback methods ---

    override fun onDataReceived(data: WECampaignData) {
        Log.d(TAG, "onDataReceived: targetViewId=${data.targetViewId}")

        if (data.targetViewId == SIMILAR_PRODUCTS_PROPERTY_ID) {
            val customData = data.content?.customData
            handleSimilarProductsData(customData)
        }
    }

    override fun onPlaceholderException(
        campaignId: String?,
        targetViewId: String,
        error: Exception
    ) {
        Log.e(TAG, "onPlaceholderException: targetViewId=$targetViewId, error=${error.message}")
    }

    override fun onRendered(data: WECampaignData) {
        Log.d(TAG, "onRendered: targetViewId=${data.targetViewId}")
    }

    // --- Similar Products custom data handling ---

    private fun handleSimilarProductsData(customData: Map<String, Any>?) {
        if (customData == null) {
            Log.e(TAG, "customData is null for Similar Products")
            return
        }

        val similarProductsJson = customData["similarProducts"] as? String
        if (similarProductsJson == null) {
            Log.e(TAG, "similarProducts key not found in customData")
            return
        }

        try {
            val jsonObject = JSONObject(similarProductsJson)
            val productsArray = jsonObject.getJSONArray("products")
            val similarProducts = mutableListOf<SimilarProduct>()

            for (i in 0 until productsArray.length()) {
                val productObj = productsArray.getJSONObject(i)
                similarProducts.add(
                    SimilarProduct(
                        image = productObj.getString("image"),
                        title = productObj.getString("title"),
                        price = productObj.getString("price"),
                        deeplink = productObj.getString("deeplink")
                    )
                )
            }

            runOnUiThread {
                showSimilarProducts(similarProducts)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing similar products data: ${e.message}")
        }
    }

    private fun showSimilarProducts(products: List<SimilarProduct>) {
        if (products.isEmpty()) return

        similarProductsContainer.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        similarProductsRecyclerView.layoutManager = layoutManager
        similarProductsRecyclerView.isNestedScrollingEnabled = false

        val adapter = SimilarProductsAdapter(products) { product ->
            Log.d(TAG, "Similar product clicked - deeplink: ${product.deeplink}")
            // Open the deeplink
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.deeplink))
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error opening deeplink: ${e.message}")
            }
        }
        similarProductsRecyclerView.adapter = adapter
    }

    // --- Existing methods ---

    private fun populateDetails(product: Product?) {
        val imageView = findViewById<ImageView>(R.id.productImageView)
        val productTiletextView = findViewById<TextView>(R.id.titleTextView)
        val productPriceTextView = findViewById<TextView>(R.id.priceTextView)
        val imageResId = resources.getIdentifier(product?.image, "drawable", packageName)
        imageView.setImageResource(imageResId)
        productTiletextView.text = product?.title
        productPriceTextView.text = product?.price
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).title = product?.title
        setSupportActionBar(findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun sendAddToCartResult(product: Product?) {
        val resultIntent = Intent()
        resultIntent.putExtra("added", true)
        val addedToCartAttributes: MutableMap<String, Any> = HashMap()
        if (product != null) {
            addedToCartAttributes["Title"] = product.title
            addedToCartAttributes["price"] = product.price
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
