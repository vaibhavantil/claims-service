package com.hedvig.claims.events.upcast

import com.hedvig.claims.events.BackofficeClaimCreatedEvent
import com.hedvig.claims.events.ClaimFileUploadedEvent
import com.hedvig.claims.query.UploadSource
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster

class BackOfficeClaimCreatedEvent_v1 : SingleEventUpcaster() {

    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
        return intermediateRepresentation.type == targetType
    }

    override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {

        return intermediateRepresentation.upcastPayload(
            targetType,
            org.dom4j.Document::class.java
        ) { document ->
            val element = document.rootElement
            element.addElement("contractId").data = null
            document
        }
    }

    companion object {
        private val targetType = SimpleSerializedType(
            BackofficeClaimCreatedEvent::class.java.typeName, null
        )
    }
}
