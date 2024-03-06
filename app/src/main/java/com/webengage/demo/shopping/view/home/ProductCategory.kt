package com.webengage.demo.shopping.view.home

import java.io.Serializable

data class ProductCategory(val title: String,
                           val products: List<Product>
)
data class Product(
    val image: String,
    val title: String,
    val price: String
): Serializable
