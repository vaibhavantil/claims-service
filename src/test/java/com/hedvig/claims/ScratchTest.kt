package com.hedvig.claims

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hedvig.claims.web.dto.CreatePaymentDto
import com.hedvig.claims.web.dto.PaymentRequestDTO
import com.hedvig.claims.web.dto.PaymentType
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.javamoney.moneta.Money
import org.zalando.jackson.datatype.money.MoneyModule

import com.fasterxml.jackson.databind.SerializationFeature




class ScratchTest {


    @Test
    fun testMe() {
        val tester = CreatePaymentDto(
            UUID.randomUUID().toString(),
            Money.of(BigDecimal("123.45"), "SEK"),
            Money.of(BigDecimal("678.90"), "SEK"),
            "A note!",
            false,
            PaymentType.IndemnityCost,
            "A Handler Reference",
            false
        )

        val om = ObjectMapper()
        om.enable(SerializationFeature.INDENT_OUTPUT)
        om.registerModule(MoneyModule())
        val output = om.writeValueAsString(tester)
        assertNotNull(output)

        val input = om.readValue<CreatePaymentDto>(output)
        assertNotNull(input)

        assertEquals(tester, input)
    }


    @Test
    fun testAuto() {
        val tester = PaymentRequestDTO(
            UUID.randomUUID(),
            Money.of(BigDecimal("123.45"), "SEK"),
            Money.of(BigDecimal("678.90"), "SEK"),
            "A Handler Reference",
            false,
            "A note!",
            false
        )

        val om = ObjectMapper()
        om.enable(SerializationFeature.INDENT_OUTPUT)
        om.registerModule(MoneyModule())
        val output = om.writeValueAsString(tester)

        assertNotNull(output)

        val input = om.readValue<PaymentRequestDTO>(output)
        assertNotNull(input)

        assertEquals(tester, input)
    }
}

