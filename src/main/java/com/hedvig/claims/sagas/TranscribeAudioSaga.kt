package com.hedvig.claims.sagas

import com.hedvig.claims.commands.TranscribeAudioCommand
import com.hedvig.claims.commands.UpdateClaimTypeCommand
import com.hedvig.claims.events.ClaimCreatedEvent
import com.hedvig.claims.serviceIntegration.predictor.Predictor
import com.hedvig.homer.SpeechToTextService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@Saga
class TranscribeAudioSaga {

    @Autowired
    @Transient
    lateinit var commandGateway: CommandGateway

    @Autowired
    @Transient
    lateinit var speechToTextService: SpeechToTextService

    @Autowired
    @Transient
    lateinit var predictor: Predictor

    @StartSaga
    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    fun onClaimCreated(event: ClaimCreatedEvent) {
        try {
            val result = speechToTextService.convertSpeechToText(event.audioURL, event.id)
            if (result.text.isNotBlank() && result.languageCode.isNotBlank() && result.confidence != 0f) {
                commandGateway.sendAndWait<Void>(
                    TranscribeAudioCommand(
                        event.id,
                        result.text,
                        result.confidence,
                        result.languageCode
                    )
                )

                if (predictor.predictIfItsAccidentClaim(result.text)) {
                    commandGateway.sendAndWait<Void>(
                        UpdateClaimTypeCommand(
                            event.id,
                            event.userId,
                            LocalDateTime.now(),
                            DRULLE
                        )
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("Caught exception transcribing audio", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        const val DRULLE = "DRULLE"
    }
}
