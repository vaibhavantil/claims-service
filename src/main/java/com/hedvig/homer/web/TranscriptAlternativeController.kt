package com.hedvig.homer.web

import com.hedvig.claims.query.ClaimsRepository
import com.hedvig.homer.SpeechToTextService
import com.hedvig.homer.repository.ClaimTranscription
import com.hedvig.homer.repository.ClaimTranscriptionAlternative
import com.hedvig.homer.repository.ClaimTranscriptionRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
class TranscriptAlternativeController(
	private val claimsRepository: ClaimsRepository,
	private val claimTranscriptionRepository: ClaimTranscriptionRepository,
	private val speechToTextService: SpeechToTextService,
) {
	@RequestMapping(
		path = ["/fillAlternatives"],
		method = [RequestMethod.POST]
	)
	fun fillAlternatives() {
        val claims = claimsRepository.findAll()
        val transcriptions = claimTranscriptionRepository.findAll()

        transcriptions.forEach {
            claims.remove(it.claim)
        }
		log.info("Filling ${claims.size} claims")

		var numTranscribed = 0
		var numNull = 0

		claims.forEach { claimEntity ->
			Thread.sleep(50)
			if (claimEntity.audioURL != null) {
				try {
					log.info("Backfilling audio for claim ${claimEntity.id} -  Started")
					val result = speechToTextService.convertSpeechToText(
                        claimEntity.audioURL,
                        claimEntity.id,
                        5
                    )

					if (result.text.isNotBlank() && result.languageCode.isNotBlank() && result.confidence != 0f) {
						claimTranscriptionRepository.save(
							ClaimTranscription(
                                bestTranscript = result.text,
                                alternativesList = result.alternatives.map {
                                    ClaimTranscriptionAlternative(transcript = it)
                                }.toMutableList(),
                                claim = claimEntity,
                                confidenceScore = result.confidence,
                                languageCode = result.languageCode
							)
						)
					}
					log.info("Backfilling audio for claim ${claimEntity.id}  -  Ended")
					numTranscribed++
				} catch (e: Exception) {
					log.error("Backfilling audio for claim ${claimEntity.id} - Caught exception transcribing audio", e)
				}
			} else {
				log.info("audio url null, skipping")
				numNull++
			}
		}
		log.info("Backfilling finished with $numTranscribed successfully transcribed recordings " +
			", $numNull missing audio urls and ${claims.size - numTranscribed} exceptions")
	}

	companion object {
		val log = LoggerFactory.getLogger(TranscriptAlternativeController::class.java)!!
	}
}

