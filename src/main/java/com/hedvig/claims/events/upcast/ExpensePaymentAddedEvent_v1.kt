package com.hedvig.claims.events.upcast

import com.hedvig.claims.events.ExpensePaymentAddedEvent
import com.hedvig.claims.query.Carrier
import lombok.Value
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document

@Value
class ExpensePaymentAddedEvent_v1 : SingleEventUpcaster() {
    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
        return intermediateRepresentation.type == targetType
    }

    override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {
        return intermediateRepresentation.upcastPayload(
            outputType,
            Document::class.java
        ) { document ->
            val rootElement = document.rootElement
            rootElement.addElement("carrier").text = Carrier.HDI.name
            document
        }
    }

    companion object {
        private val targetType = SimpleSerializedType(
            ExpensePaymentAddedEvent::class.java.typeName, null
        )

        private val outputType = SimpleSerializedType(
            ExpensePaymentAddedEvent::class.java.typeName, "1.0"
        )
    }
}
