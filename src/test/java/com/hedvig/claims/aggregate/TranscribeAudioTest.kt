package com.hedvig.claims.aggregate

import com.hedvig.claims.aggregates.ClaimsAggregate
import com.hedvig.claims.commands.TranscribeAudioCommand
import com.hedvig.claims.events.AudioTranscribedEvent
import com.hedvig.claims.events.ClaimCreatedEvent
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Test

class TranscribeAudioTest {

    @Test
    fun `command with no previous events emitts event`() {
        val fixture = AggregateTestFixture(ClaimsAggregate::class.java)

        fixture
            .given(ClaimCreatedEvent("id", "userId", "audioUrl", null))
            .`when`(TranscribeAudioCommand("id","A text", 1.0f, "sv-EN"))
            .expectSuccessfulHandlerExecution()
            .expectEvents(AudioTranscribedEvent("id", "A text", 1.0f, "sv-EN"))
            .expectState {
                assertThat(it.transcriptionResult.text).isEqualTo("A text")
                assertThat(it.transcriptionResult.confidence).isEqualTo(1.0f)
            }
    }

    @Test
    fun `aggregate with previous event emits event replaces old value`() {
        val fixture = AggregateTestFixture(ClaimsAggregate::class.java)

        fixture
            .given(
                ClaimCreatedEvent("id", "userId", "audioUrl", null),
                TranscribeAudioCommand("id","A text", 1.0f, "sv-EN"))
            .`when`(TranscribeAudioCommand("id","A new text", 0.7f, "sv-EN"))
            .expectSuccessfulHandlerExecution()
            .expectEvents(AudioTranscribedEvent("id", "A new text", 0.7f, "sv-EN"))
            .expectState {
                assertThat(it.transcriptionResult.text).isEqualTo("A new text")
                assertThat(it.transcriptionResult.confidence).isEqualTo(.7f)
            }
    }

}
