package com.webengage.demo.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.webengage.demo.shopping.view.home.Product

class CartViewModel : ViewModel() {
    private val itemList = MutableLiveData<List<Product>>()

    fun getItemList(): LiveData<List<Product>> {
        return itemList
    }

    fun updateItemList(product: Product) {
        itemList.value = itemList.value?.plus(product) ?: listOf(product)
    }

    fun removeItemFromList(product: Product) {
        val list = itemList.value?.toMutableList()
        list?.remove(product)
        itemList.value = list

    }

    fun removeAllItems() {
        if (itemList != null && itemList.value?.size!! > 0) {
            val list = itemList.value?.toMutableList()
            list?.removeAll(list)
            itemList.value = list
        }

    }
}