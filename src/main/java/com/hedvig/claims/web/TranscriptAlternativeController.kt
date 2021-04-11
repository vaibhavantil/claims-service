package com.hedvig.claims.web

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
		val list = claimsRepository.findAll()
		log.info("Filling ${list.size} claims")
		var numTranscribed = 0
		var numNull = 0
		list.forEach {
			Thread.sleep(50)
			if (it.audioURL != null) {
				try {
					log.info("Backfilling audio for claim ${it.id} -  Started")
					val claimId = it.id
					val result = speechToTextService.convertSpeechToText(it.audioURL, claimId, 5)
					if (result.text.isNotBlank() && result.languageCode.isNotBlank() && result.confidence != 0f) {
						claimTranscriptionRepository.save(
							ClaimTranscription().also {
								it.bestTranscript = result.text
								it.alternative = result.alternatives.map {
									ClaimTranscriptionAlternative.from(it)
								}.toMutableList()
								it.claimId = claimId
								it.confidenceScore = result.confidence
							}
						)

					}
					log.info("Backfilling audio for claim ${it.id}  -  Ended")
					numTranscribed++
				} catch (e: Exception) {
					log.error("Backfilling audio for claim ${it.id} - Caught exception transcribing audio", e)
				}
			} else {
				log.info("audio url null, skipping")
				numNull++
			}
		}
		log.info("Backfilling finished with $numTranscribed successfully transcribed recordings " +
			", $numNull missing audio urls and ${list.size - numTranscribed} exceptions")
	}

	companion object {
		val log = LoggerFactory.getLogger(TranscriptAlternativeController::class.java)!!
	}
}

