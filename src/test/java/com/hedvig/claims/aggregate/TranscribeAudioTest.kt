package com.hedvig.claims.aggregate

import com.hedvig.claims.aggregates.ClaimsAggregate
import com.hedvig.claims.commands.AudioTranscribedCommand
import com.hedvig.claims.events.AudioTranscribedEvent
import com.hedvig.claims.events.ClaimCreatedEvent
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Test

class TranscribeAudioTest {

    @Test
    fun test() {
        val fixture = AggregateTestFixture(ClaimsAggregate::class.java)

        fixture
            .given(ClaimCreatedEvent("id", "userId", "audioUrl"))
            .`when`(AudioTranscribedCommand("id","A text", 1.0f))
            .expectSuccessfulHandlerExecution()
            .expectEvents(AudioTranscribedEvent("A text", 1.0f))
            .expectState {
                assertThat(it.transcritptionResut.text).isEqualTo("A text")
                assertThat(it.transcritptionResut.confidence).isEqualTo(1.0f)
            }
    }

}
