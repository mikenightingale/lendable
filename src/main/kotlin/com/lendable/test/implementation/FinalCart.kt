package com.lendable.test.implementation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.math.BigDecimal

data class FinalCart( val items:List<CartItem>, val is2ForOne: Boolean, val total: BigDecimal){
    private val mapper = jacksonObjectMapper()

    fun asJSON() : String{
        return mapper.writeValueAsString(this)
    }

}