package com.hedvig.claims.events.upcast

import com.hedvig.claims.events.ClaimCreatedEvent
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document


class ClaimCreatedEvent_v2 : SingleEventUpcaster() {
    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
        val initialEvent = SimpleSerializedType(ClaimCreatedEvent::class.java.typeName, null)
        val firstRevision = SimpleSerializedType(ClaimCreatedEvent_v1::class.java.typeName, "1.0")

        return (intermediateRepresentation.type == initialEvent && intermediateRepresentation.type.revision == null) ||
                (intermediateRepresentation.type == firstRevision && intermediateRepresentation.type.revision == firstRevision.revision)
    }

    override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {
        return intermediateRepresentation.upcastPayload(
            SimpleSerializedType(
                ClaimCreatedEvent::class.java.typeName,
                "2.0"
            ),
            Document::class.java
        ) { document: Document ->
            val root = document.rootElement
            root.remove(root.element("contractId"))
            root.addElement("contractId").data = null
            document
        }
    }

}
