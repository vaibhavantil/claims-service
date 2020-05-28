package com.hedvig.claims.sagas

import com.hedvig.claims.commands.AudioTranscribedCommand
import com.hedvig.claims.events.ClaimCreatedEvent
import com.hedvig.claims.events.upcast.ClaimCreatedEvent_v1
import com.hedvig.homer.SpeechToTextService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@Saga
class TranscribeAudioSaga {

    @Autowired
    @Transient
    lateinit var commandGateway: CommandGateway

    @Autowired
    @Transient
    lateinit var speechToTextService: SpeechToTextService

    @StartSaga
    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    fun onClaimCreated(evt: ClaimCreatedEvent) {

        try {
            val result = speechToTextService.convertSpeechToText(evt.audioURL, evt.id)
            commandGateway.send<Void>(AudioTranscribedCommand(evt.id, result.text, result.confidence))
        }catch (e :Exception) {
            logger.error("Caught exception transcribing audio", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

}
