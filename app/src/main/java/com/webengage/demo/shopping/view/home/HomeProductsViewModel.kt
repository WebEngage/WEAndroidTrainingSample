package com.webengage.demo.shopping.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webengage.demo.shopping.Constants
import org.json.JSONArray

class HomeProductsViewModel : ViewModel() {
    private val productsArray = JSONArray(Constants.productsDataJSON)
    private val productMap: MutableLiveData<MutableList<ProductCategory>> = MutableLiveData()

    fun getProductsData(): LiveData<MutableList<ProductCategory>> {
        return productMap
    }

    fun fetchProducts(): Product? {
        val categoryData =  mutableListOf<ProductCategory>()
        var clickedProduct: Product? = null
//
//        if (productMap.value != null
//            && productMap.value!!.products.isNotEmpty()
//        ) {
//            return null
//        }

        for (i in 0 until productsArray.length()) {
            val categoryObject = productsArray.getJSONObject(i)

            val title = categoryObject.getString("title")
            val productsArray = categoryObject.getJSONArray("products")

            val products = mutableListOf<Product>()

            // Iterate through the products array
            for (j in 0 until productsArray.length()) {
                val productObject = productsArray.getJSONObject(j)

                val image = productObject.getString("image")
                val productTitle = productObject.getString("title")
                val price = productObject.getString("price")

                val product = Product(image, productTitle, price)
                products.add(product)
                clickedProduct = product
            }
            val category = ProductCategory(title, products)
            categoryData.add(category)
        }
        productMap.value = mutableListOf()
        productMap.value!!.addAll(categoryData)
        return clickedProduct

    }


}