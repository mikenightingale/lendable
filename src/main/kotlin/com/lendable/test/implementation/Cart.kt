package com.lendable.test.implementation

import com.lendable.test.model.ICart
import com.lendable.test.model.IMutableCart
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean


class Cart private constructor(private val userId: Int) : IMutableCart, ICart {

    private var isTwoForOne: AtomicBoolean = AtomicBoolean(false)
    private var items: MutableList<CartItem> = ArrayList<CartItem>()

    companion object {
        private val carts: ConcurrentHashMap<Int, Cart> = ConcurrentHashMap<Int, Cart>()
        fun provide(userId: Int): Cart {
            if (carts.containsKey(userId)) {
                return carts[userId]!!;
            }

            val new = Cart(userId)
            carts[userId] = new;
            return new
        }

        fun clearAll() {
            carts.clear();
        }
    }

    override fun items(): List<CartItem> {
        // clone to prevent reference escape
        return ArrayList(items);
    }

    override fun total(): BigDecimal {
        synchronized(this) {
            var toBigDecimal = (this.items().stream().map { x -> linePrice(x) }.reduce(::calcTotal)
                .orElse(0) / 100F).toBigDecimal()
            toBigDecimal = toBigDecimal.setScale(2, RoundingMode.HALF_UP)
            return toBigDecimal
        }
    }

    private fun linePrice(x: CartItem): Int {

        if (isTwoForOne.get()) {
            val quotientAndReminder = getQuotientAndReminder(x.quantity, 2)
            return (quotientAndReminder.first * x.itemPriceInCents) + (quotientAndReminder.second * x.itemPriceInCents)
        }
        return x.itemPriceInCents * x.quantity
    }

    fun getQuotientAndReminder(dividend: Int, divisor: Int): Pair<Int, Int> {
        return dividend / divisor to dividend % divisor
    }

    private fun calcTotal(x: Int, y: Int): Int {
        return x + y
    }


    override fun addItem(item: CartItem): Int {
        synchronized(this) {
            var match: CartItem? = getCartItem(item.productSKU)

            if (match == null) {
                items.add(item)
            } else {
                items.remove(match)
                items.add(match.cloneWithQuantity(match.quantity + item.quantity))
            }
            return items.size;
        }
    }

    override fun removeItem(item: CartItem): Int {
        synchronized(this) {
            items.remove(item)
            return items.size;
        }
    }

    override fun removeItem(sku: String): Int {
        synchronized(this) {
            val match = getCartItem(sku)
            items.remove(match)
            return items.size;
        }
    }


    override fun setTwoForOne() {
        isTwoForOne.compareAndSet(false, true)
    }


    override fun clearTwoForOne() {
        isTwoForOne.compareAndSet(true, false);
    }

    override fun finalCart(): FinalCart {
        synchronized(this) {
            return FinalCart(this.items(), this.isTwoForOne.get(), this.total())
        }
    }

    fun getCartItem(sku: String) = synchronized(this) {
        items.firstOrNull({ it.productSKU == sku })
    }


    override fun toString(): String {
        return "Cart(items=$items, userId=$userId, isTwoForOne=$isTwoForOne)"
    }


}
