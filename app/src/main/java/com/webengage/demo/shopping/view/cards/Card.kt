package com.webengage.demo.shopping.view.cards

data class Card(
    val id: String,
    val name: String,
    val tier: String?,
    val limit: String,
    val description: String,
    val imageRes: String
)
