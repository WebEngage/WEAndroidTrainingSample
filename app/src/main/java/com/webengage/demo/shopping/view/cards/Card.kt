package com.webengage.demo.shopping.view.cards

data class Card(
    val id: String,
    val name: String,
    val tier: String?,
    val limit: String,
    val annualFee: String,
    val description: String,
    val imageRes: String,
    val features: List<String>
)
