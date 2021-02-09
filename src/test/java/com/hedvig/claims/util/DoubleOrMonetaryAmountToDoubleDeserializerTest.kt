package com.hedvig.claims.util

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Test
import org.zalando.jackson.datatype.money.MoneyModule

class DoubleOrMonetaryAmountToDoubleDeserializerTest {

    private data class DoubleTester(
        @JsonProperty("amount")
        @JsonDeserialize(using = DoubleOrMonetaryAmountToDoubleDeserializer::class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        val amount: Double?,

        @JsonProperty("deductible")
        @JsonDeserialize(using = DoubleOrMonetaryAmountToDoubleDeserializer::class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        val deductible: Double?
    )

    @Test
    fun `deserializer should work for Double`() {
        val jsonString = """
            {
              "amount": 1000.00,
              "deductible": 100.00
            }
            """.trimIndent()

        val mapper = ObjectMapper()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.registerModule(MoneyModule())

        val paymentDto = mapper.readValue<DoubleTester>(jsonString, DoubleTester::class.java)

        assertNotNull(paymentDto)
        assertNotNull(paymentDto.amount)
        assertEquals(1000.00, paymentDto.amount)

        assertNotNull(paymentDto.deductible)
        assertEquals(100.00, paymentDto.deductible)
    }

    @Test
    fun `deserializer should work for MonetaryAmount`() {
        val jsonString = """
            {
              "amount": {
                "amount": "1000.00",
                "currency": "SEK"
              },
              "deductible": {
                "amount": "100.00",
                "currency": "SEK"
              }
            }
            """.trimIndent()

        val mapper = ObjectMapper()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.registerModule(MoneyModule())

        val paymentDto = mapper.readValue<DoubleTester>(jsonString, DoubleTester::class.java)

        assertNotNull(paymentDto)
        assertNotNull(paymentDto.amount)
        assertEquals(1000.00, paymentDto.amount)

        assertNotNull(paymentDto.deductible)
        assertEquals(100.00, paymentDto.deductible)
    }

    @Test
    fun `deserializer should work for either Double or MonetaryAmount`() {
        val jsonString = """
            {
              "amount": {
                "amount": "1000.00",
                "currency": "SEK"
              },
              "deductible": 100.00
            }
            """.trimIndent()

        val mapper = ObjectMapper()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.registerModule(MoneyModule())

        val paymentDto = mapper.readValue<DoubleTester>(jsonString, DoubleTester::class.java)

        assertNotNull(paymentDto)
        assertNotNull(paymentDto.amount)
        assertEquals(1000.00, paymentDto.amount)

        assertNotNull(paymentDto.deductible)
        assertEquals(100.00, paymentDto.deductible)
    }
}

