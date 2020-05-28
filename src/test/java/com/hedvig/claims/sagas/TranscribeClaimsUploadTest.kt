package com.hedvig.claims.sagas

import com.hedvig.claims.commands.AudioTranscribedCommand
import com.hedvig.claims.events.ClaimCreatedEvent
import com.hedvig.homer.SpeechToTextService
import com.hedvig.homer.handlers.SpeechToTextResult
import io.mockk.every
import io.mockk.mockk
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Test
import java.util.UUID

class TranscribeClaimsUploadTest {

    @Test
    fun `first test`() {

        val event = ClaimCreatedEvent(
            "aClaimId",
            "aUserId", "theUrl:)"
        )

        val textToSpeechService = mockk<SpeechToTextService>()

        every { textToSpeechService.convertSpeechToText(any(), any()) } returns SpeechToTextResult("A weird text", 1.0f)

        val sagaFixture = SagaTestFixture(TranscribeAudioSaga::class.java)
        sagaFixture.registerResource(textToSpeechService)

        sagaFixture
            .whenPublishingA(event)
                .expectDispatchedCommands(AudioTranscribedCommand("A weird text", confidence = 1.0f))
    }
}
