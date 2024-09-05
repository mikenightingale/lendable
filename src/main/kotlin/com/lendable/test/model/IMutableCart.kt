package com.lendable.test.model

import com.lendable.test.implementation.CartItem
import com.lendable.test.implementation.FinalCart

interface IMutableCart {
    fun addItem(item: CartItem): Int
    fun removeItem(item: CartItem): Int
    fun removeItem(sku: String): Int
    fun setTwoForOne()
    fun clearTwoForOne()
    fun finalCart(): FinalCart;
}