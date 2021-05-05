package com.hedvig.claims.events.upcast

import com.hedvig.claims.events.ExpensePaymentAddedEvent
import lombok.Value
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document

@Value
class ExpensePaymentAddedEvent_v2 : SingleEventUpcaster() {
    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
        return intermediateRepresentation.type == targetType
    }

    override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {
        return intermediateRepresentation.upcastPayload(
            outputType,
            Document::class.java
        ) { document ->
            val rootElement = document.rootElement
            val amount = rootElement.element("amount")
            val amountCurrency = amount.element("currency")
            val amountAttribute = amountCurrency.attribute("class")
            if (amountAttribute.value == "org.javamoney.moneta.internal.JDKCurrencyAdapter") {
                amountCurrency.remove(amountAttribute)
                amountCurrency.addAttribute("class", "org.javamoney.moneta.spi.JDKCurrencyAdapter")
            }

            val deductible = rootElement.element("deductible")
            val deductibleCurrency = deductible.element("currency")
            val deductibleAttribute = deductibleCurrency.attribute("class")
            if (deductibleAttribute.value == "org.javamoney.moneta.internal.JDKCurrencyAdapter") {
                deductibleCurrency.remove(deductibleAttribute)
                deductibleCurrency.addAttribute("class", "org.javamoney.moneta.spi.JDKCurrencyAdapter")
            }

            document
        }
    }

    companion object {
        private val targetType = SimpleSerializedType(
            ExpensePaymentAddedEvent::class.java.typeName, "1.0"
        )

        private val outputType = SimpleSerializedType(
            ExpensePaymentAddedEvent::class.java.typeName, "2.0"
        )
    }
}
