package com.lendable.test.model

import com.lendable.test.implementation.CartItem
import java.math.BigDecimal

interface ICart {
    fun total(): BigDecimal
    fun items(): List<CartItem>
}