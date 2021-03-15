package com.hedvig.claims.upcast

import com.hedvig.claims.events.AutomaticPaymentAddedEvent
import com.hedvig.claims.events.PayoutDetails
import com.hedvig.claims.events.upcast.AutomaticPaymentAddedEventUpcaster_v3
import org.assertj.core.api.Assertions.assertThat

import java.util.stream.Collectors.toList
import java.util.stream.Stream
import org.axonframework.eventsourcing.GenericDomainEventMessage
import org.axonframework.eventsourcing.eventstore.jpa.DomainEventEntry
import org.axonframework.messaging.MetaData
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.upcasting.event.InitialEventRepresentation
import org.axonframework.serialization.xml.XStreamSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.springframework.test.util.ReflectionTestUtils

class AutomaticPaymentAddedUpcastV3Test {

    @Test
    fun `upcast AutomaticPaymentAdded null to v3`() {

        val serializer = XStreamSerializer()
        val metaData = MetaData.with("key", "value")
        val eventData = DomainEventEntry(
            GenericDomainEventMessage(
                "test", "aggregateId", 0,
                "This will be replaced", metaData
            ),
            serializer
        )

        ReflectionTestUtils.setField(eventData, "payload", payload.toByteArray())
        ReflectionTestUtils.setField(
            eventData,
            "payloadType",
            "com.hedvig.claims.events.AutomaticPaymentAddedEvent"
        )
        ReflectionTestUtils.setField(eventData, "payloadRevision", null)


        val result =
            AutomaticPaymentAddedEventUpcaster_v3().upcast(Stream.of(InitialEventRepresentation(eventData, serializer)))
                .collect(toList())
        assertFalse(result.isEmpty())

        val firstEvent = result.get(0)
        assertEquals("3.0", firstEvent.getType().getRevision())

        val upcastedEvent =
            serializer.deserialize<AutomaticPaymentAddedEvent, AutomaticPaymentAddedEvent>(firstEvent.getData() as SerializedObject<AutomaticPaymentAddedEvent>)

        assertThat(upcastedEvent.payoutDetails).isInstanceOf(PayoutDetails.NotSelected::class.java)
    }


    private val payload =
        "<com.hedvig.claims.events.AutomaticPaymentAddedEvent><Id>id</Id><claimId>claimId</claimId><memberId>memberId</memberId><amount class=\"org.javamoney.moneta.Money\"><currency class=\"org.javamoney.moneta.spi.JDKCurrencyAdapter\"><baseCurrency>SEK</baseCurrency><context><data><entry><string>provider</string><string>java.util.Currency</string></entry></data></context></currency><monetaryContext><data><entry><string>amountType</string><java-class>org.javamoney.moneta.Money</java-class></entry><entry><string>java.lang.Class</string><java-class>org.javamoney.moneta.Money</java-class></entry><entry><string>precision</string><int>256</int></entry><entry><string>java.math.RoundingMode</string><java.math.RoundingMode>HALF_EVEN</java.math.RoundingMode></entry></data></monetaryContext><number>10</number></amount><deductible class=\"org.javamoney.moneta.Money\"><currency class=\"org.javamoney.moneta.spi.JDKCurrencyAdapter\" reference=\"../../amount/currency\"/><monetaryContext reference=\"../../amount/monetaryContext\"/><number>10</number></deductible><note>note</note><isExGracia>false</isExGracia><handlerReference></handlerReference><sanctionCheckSkipped>true</sanctionCheckSkipped></com.hedvig.claims.events.AutomaticPaymentAddedEvent>"
}
