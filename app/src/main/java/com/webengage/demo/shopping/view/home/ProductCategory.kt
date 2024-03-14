package com.webengage.demo.shopping.view.home

import java.io.Serializable

data class ProductCategory(val title: String,
                           val products: MutableList<Product>
)
data class Product(
    val image: String,
    val title: String,
    val price: String,
    val type: String
): Serializable
