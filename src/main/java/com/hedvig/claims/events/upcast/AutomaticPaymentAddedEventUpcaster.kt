package com.hedvig.claims.events.upcast

import com.hedvig.claims.events.AutomaticPaymentAddedEvent
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document

class AutomaticPaymentAddedEventUpcaster : SingleEventUpcaster() {

    private val targetType = SimpleSerializedType(
        AutomaticPaymentAddedEvent::class.java.typeName, null
    )

    override fun canUpcast(intermediateEventRepresentation: IntermediateEventRepresentation) =
        intermediateEventRepresentation.type == targetType

    override fun doUpcast(intermediateEventRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {
        return intermediateEventRepresentation.upcastPayload(
            SimpleSerializedType(
                targetType.name,
                "1.0"),
            Document::class.java
        ) { document: Document ->
            val element = document.rootElement
            element.addElement("payoutDetails")
            val payoutDetails = element.element("payoutDetails")
            payoutDetails.addAttribute("class", "com.hedvig.claims.events.PayoutDetails\$NotSelected")

            document
        }
    }
}
