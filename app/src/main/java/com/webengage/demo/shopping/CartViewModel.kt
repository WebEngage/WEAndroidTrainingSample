package com.webengage.demo.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartViewModel: ViewModel() {
    private val itemList = MutableLiveData<List<Product>>()

    fun getItemList(): LiveData<List<Product>> {
        return itemList
    }

    fun updateItemList(product: Product) {
        Log.d("CartViewModel", "updateItemList "+product.title)
        itemList.value = itemList.value?.plus(product) ?: listOf(product)
        if(itemList.value != null)
            Log.d("CartViewModel", "updateItemList itemList size "+ itemList.value!!.size)
    }

    fun removeItemFromList(product: Product) {
        Log.d("CartViewModel", "removeItemFromList "+product.title)
        Log.d("CartViewModel", "removeItemFromList size "+itemList.value?.size)
        val itemIndex = itemList.value?.indexOf(product)
        val list = itemList.value?.toMutableList()
        list?.remove(product)
        itemList.value = list
        Log.d("CartViewModel", "removeItemFromList size "+itemList.value?.size)
    }
}