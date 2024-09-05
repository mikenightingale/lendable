package com.lendable.test.implementation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val USER_ID = 1


private const val CART_AS_JSON =
    "{\"items\":[{\"productSKU\":\"123456789\",\"productName\":\"Cornflakes\",\"shortDescription\":\"Big Box 400g\",\"quantity\":2,\"itemPriceInCents\":235,\"linePrice\":2.35}],\"is2ForOne\":true,\"total\":2.35}"

private const val CART_TO_STRING =
    "FinalCart(items=[CartItem(productSKU=123456789, productName=Cornflakes, shortDescription=Big Box 400g, quantity=2, itemPriceInCents=235)], is2ForOne=true, total=2.35)"

class CartTest {

    private lateinit var cart: Cart

    @BeforeEach
    fun setUp() {
        Cart.clearAll()
        cart = Cart.provide(USER_ID)
    }

    @Test
    fun shouldProvideSingleCartPerUser() {
        cart.addItem(provideItem(sku = "123456789"))
        cart = Cart.provide(USER_ID)
        assertThat(cart.items()).hasSize(1)
    }


    @Test
    fun shouldAddItemToCard() {
        val count = cart.addItem(provideItem(1, "Cornflakes", "123456789"))
        assertThat(count).isEqualTo(1)
    }

    @Test
    fun shouldRemoveItem() {
        val item = provideItem(1, "Cornflakes", "123456789")
        cart.addItem(item)
        val count = cart.removeItem(item)
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun shouldRemoveItemBySKU() {
        val item = provideItem(1, "Cornflakes", "123456789")
        cart.addItem(item)
        val count = cart.removeItem(item.productSKU)
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun shouldAddItemsWithSameSKU() {
        val item = provideItem(1, "Cornflakes", "123456789")
        cart.addItem(item)
        cart.addItem(item)
        val count = cart.addItem(item)
        assertThat(count).isEqualTo(1)
        assertThat(cart.items().first().quantity).isEqualTo(3)
    }

    @Test
    fun shouldListContents() {
        val item = provideItem(1, "Cornflakes", "123456789")
        cart.addItem(item)
        val first = cart.items().first()
        assertThat(first).isEqualTo(item)
    }

    @Test
    fun shouldApplyTwoForOne() {
        cart.addItem(provideItem(2, sku = "123456789"))
        cart.setTwoForOne()
        assertThat(cart.items().first().quantity).isEqualTo(2)
        assertThat(cart.total().toPlainString()).isEqualTo("2.35")
    }

    @Test
    fun shouldApplyTwoForOneAllEligibleItems() {
        cart.addItem(provideItem(2, sku = "123456789"))
        cart.addItem(provideItem(1, "Coco pops", "234567890"))
        cart.setTwoForOne()
        assertThat(cart.items().first().quantity).isEqualTo(2)
        assertThat(cart.items().last().quantity).isEqualTo(1)
        assertThat(cart.total().toString()).isEqualTo("4.70")
    }

    @Test
    fun shouldApplyTwoForOne3Items() {
        cart.addItem(provideItem(3, sku = "123456789"))
        cart.setTwoForOne()
        assertThat(cart.items().first().quantity).isEqualTo(3)
        assertThat(cart.total().toPlainString()).isEqualTo("4.70")

    }


    @Test
    fun shouldClearTwoForOne() {
        cart.addItem(provideItem(2, sku = "123456789"))
        cart.setTwoForOne()
        cart.clearTwoForOne()
        assertThat(cart.items().first().quantity).isEqualTo(2)
    }

    @Test
    fun shouldClearTwoForOneAllItems() {
        cart.addItem(provideItem(2, sku = "123456789"))
        cart.addItem(provideItem(1, "Coco pops", "234567890"))
        cart.setTwoForOne()
        cart.clearTwoForOne()
        assertThat(cart.items().first().quantity).isEqualTo(2)
        assertThat(cart.items().last().quantity).isEqualTo(1)
    }

    @Test
    fun shouldSetTwoForOneOnce() {
        cart.addItem(provideItem(2, sku = "123456789"))
        cart.setTwoForOne()
        cart.setTwoForOne()
        assertThat(cart.total().toPlainString()).isEqualTo("2.35")
    }

    @Test
    fun shouldClearTwoForOneOnce() {

        cart.addItem(provideItem(1, sku = "123456789"))
        cart.setTwoForOne()
        cart.clearTwoForOne()
        cart.clearTwoForOne()
        assertThat(cart.total().toPlainString()).isEqualTo("2.35")
    }

    @Test
    fun shouldCalculateTotal() {
        cart.addItem(provideItem(2, sku = "123456789"))
        cart.addItem(provideItem(2, sku = "123456789"))
        assertThat(cart.total().toPlainString()).isEqualTo("9.40")
    }

    @Test
    fun shouldIsolateFromChangeAtCheckout() {
        cart.addItem(provideItem(2, sku = "123456789"))
        cart.addItem(provideItem(2, sku = "123456789"))

        val finalCart = cart.finalCart()

        cart.addItem(provideItem(2, sku = "123456789"))

        assertThat(finalCart.total.toPlainString()).isEqualTo("9.40")
        assertThat(finalCart.items.first().quantity).isEqualTo(4)
        assertThat(finalCart.is2ForOne).isFalse()
    }

    @Test
    fun shouldImplementStringHelpers() {
        cart.addItem(provideItem(2, sku = "123456789"))
        cart.setTwoForOne()

        val finalCart = cart.finalCart()
        assertThat(finalCart.toString()).isEqualTo(CART_TO_STRING)
        assertThat(finalCart.asJSON()).isEqualTo(CART_AS_JSON)
    }

    private fun provideItem(q: Int = 1, product: String = "Cornflakes", sku: String) = CartItem(
        sku,
        product,
        "Big Box 400g",
        q,
        235
    )
}