package com.hedvig.claims.web

import com.hedvig.claims.commands.TranscribeAudioCommand
import com.hedvig.claims.query.ClaimsRepository
import com.hedvig.homer.SpeechToTextService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/i/backfill", "/_/backfill"])
class BackfillController(
    private val claimsRepository: ClaimsRepository,
    private val commandGateway: CommandGateway,
    private val speechToTextService: SpeechToTextService
) {
    @PostMapping("/audioTranscription")
    fun backfillAudioTranscription() {
        val list = claimsRepository.findAllByTranscriptionsIsNull()
        log.info("Backfilling ${list.size} claims")
        var numTranscribed = 0
        list.forEach {
            Thread.sleep(50)
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
                numTranscribed++
            } catch (e: Exception) {
                log.error("Backfilling audio for claim ${it.id} - Caught exception transcribing audio", e)
            }
        }
        log.info("Backfilling finished with $numTranscribed successfully transcribed recordings " +
                "and ${list.size - numTranscribed} exceptions")
    }

    companion object {
        val log = LoggerFactory.getLogger(BackfillController::class.java)!!
    }
}
