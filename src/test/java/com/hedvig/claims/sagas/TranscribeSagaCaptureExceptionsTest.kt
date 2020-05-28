package com.hedvig.claims.sagas

import com.hedvig.claims.events.ClaimCreatedEvent
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class TranscribeSagaCaptureExceptionsTest {

    @Test
    fun `capture exceptions`() {
        val saga = TranscribeAudioSaga()

        saga.speechToTextService = mockk()

        every { saga.speechToTextService.convertSpeechToText(any(), any()) } throws RuntimeException("Error")

        saga.onClaimCreated(ClaimCreatedEvent("", "", ""))
    }
}
