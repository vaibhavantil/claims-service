package com.hedvig.claims.web

import com.hedvig.claims.commands.TranscribeAudioCommand
import com.hedvig.claims.query.ClaimsRepository
import com.hedvig.claims.serviceIntegration.predictor.Predictor
import com.hedvig.homer.SpeechToTextService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/i/backfill", "/_/backfill"])
class BackfillController(
    private val claimsRepository: ClaimsRepository,
    private val commandGateway: CommandGateway,
    private val speechToTextService: SpeechToTextService,
    private val predictor: Predictor
) {
    @PostMapping("/audioTranscription")
    fun backfillAudioTranscription() {
        val list = claimsRepository.findAllByTranscriptionsIsNull()
        list.forEach {
            try {
                log.info("Backfilling audio for claim ${it.id} -  Started")
                val result = speechToTextService.convertSpeechToText(it.audioURL, it.id)
                if (result.text.isNotBlank() && result.languageCode.isNotBlank() && result.confidence != 0f) {
                    commandGateway.send<Void>(
                        TranscribeAudioCommand(
                            it.id,
                            result.text,
                            result.confidence,
                            result.languageCode
                        )
                    )
                }
                log.info("Backfilling audio for claim ${it.id}  -  Ended")
            } catch (e: Exception) {
                log.error("Backfilling audio for claim ${it.id} - Caught exception transcribing audio", e)
            }
        }
    }

    @PostMapping("/test")
    fun testPredictor(): ResponseEntity<Boolean> {
        return ResponseEntity.ok(predictor.predictIfItsAccidentClaim("Jag tappade min iPhone"))
    }

    companion object {
        val log = LoggerFactory.getLogger(BackfillController::class.java)!!
    }
}
