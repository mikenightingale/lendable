package com.lendable.test.implementation

import java.math.BigDecimal

data class CartItem(
    val productSKU: String,
    val productName: String,
    val shortDescription: String,
    val quantity: Int,
    val itemPriceInCents: Int
) {

    val linePrice: BigDecimal get() = (itemPriceInCents / 100f).toBigDecimal()

    fun cloneWithQuantity(quantity: Int): CartItem {
        return CartItem(
            this.productSKU,
            this.productName,
            this.shortDescription,
            quantity,
            this.itemPriceInCents
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CartItem

        return productSKU == other.productSKU
    }

    override fun hashCode(): Int {
        return productSKU.hashCode()
    }


}
