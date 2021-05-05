package com.hedvig.claims.sagas

import com.hedvig.claims.commands.TranscribeAudioCommand
import com.hedvig.claims.events.ClaimCreatedEvent
import com.hedvig.homer.SpeechToTextService
import com.hedvig.homer.handlers.SpeechToTextResult
import io.mockk.every
import io.mockk.mockk
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test

class TranscribeClaimsUploadTest {

    lateinit var textToSpeechService: SpeechToTextService
    lateinit var sagaFixture: SagaTestFixture<TranscribeAudioSaga>

    @Before
    fun setup(){
        textToSpeechService = mockk<SpeechToTextService>()
        sagaFixture = SagaTestFixture(TranscribeAudioSaga::class.java)
        sagaFixture.registerResource(textToSpeechService)
    }

    @Test
    fun `claims created event triggers transcription from SpeechToTextService`() {
        val event = ClaimCreatedEvent(
            "aClaimId",
            "aUserId",
            "theUrl:)",
            null
        )

        every { textToSpeechService.convertSpeechToText("theUrl:)", "aClaimId") } returns SpeechToTextResult(
            "A weird text",
            1.0f,
            "se-SV",
            mutableListOf("A weird text")
        )

        sagaFixture
            .whenPublishingA(event)
            .expectDispatchedCommands(TranscribeAudioCommand("aClaimId","A weird text", 1.0f, "se-SV"))
    }

    @Test
    fun `after claims created event no sagas are active`() {
        val event = ClaimCreatedEvent(
            "aClaimId",
            "aUserId",
            "theUrl:)",
            null
        )

        every { textToSpeechService.convertSpeechToText(any(), any()) } returns SpeechToTextResult(
            "A weird text",
            1.0f,
            "se-SV",
            mutableListOf("A weird text")
        )


        sagaFixture
            .whenPublishingA(event)
            .expectActiveSagas(0)
    }

    @Test
    fun `when textToSpeechSerice throws exception do nothing`() {
        val event = ClaimCreatedEvent(
            "aClaimId",
            "aUserId",
            "theUrl:)",
            null
        )

        every { textToSpeechService.convertSpeechToText(any(), any()) } throws RuntimeException("Error")

        sagaFixture
            .whenPublishingA(event)
            .expectNoDispatchedCommands()
            .expectActiveSagas(0)

    }
}
