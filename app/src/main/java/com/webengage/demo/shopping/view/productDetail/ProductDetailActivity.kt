package com.webengage.demo.shopping.view.productDetail

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.webengage.demo.shopping.R
import com.webengage.demo.shopping.view.home.Product

class ProductDetailActivity : AppCompatActivity() {
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
        populateDetails(product)

    }

    override fun onStart() {
        super.onStart()
        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("product", Product::class.java)
        } else {
            intent.getSerializableExtra("product") as Product
        }
        val data = mutableMapOf<String, Any>()
        if (product != null) {
            data["title"] = product.title
            data["price"] = product.price
        }

    }

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