package com.webengage.demo.shopping

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
        addToCartButton.setOnClickListener { sendAddToCartResult() }
        populateDetails(product)

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

    fun sendAddToCartResult() {
        val resultIntent = Intent()
        resultIntent.putExtra("added", true)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}