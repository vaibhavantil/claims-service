package com.hedvig.homer.web

import com.hedvig.homer.handlers.SpeechHandler
import com.hedvig.homer.handlers.utils.LanguageCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class HomerController(
  val speechHandler: SpeechHandler
) {
  @RequestMapping(
    path = ["/testSpeechToText"],
    method = [RequestMethod.POST]
  )
  fun test(@RequestBody aurioUrl: String): ResponseEntity<*>? {
    val claimAudioToTextSWE: SpeechHandler.SpeechResult =
      speechHandler.convertSpeechToText(aurioUrl, LanguageCode.SWEDISH)
    val claimAudioToTextGRE: SpeechHandler.SpeechResult =
      speechHandler.convertSpeechToText(aurioUrl, LanguageCode.GREEK)
    var finaltext = ""
    var finalConfidence = 0f
    if (claimAudioToTextSWE.confidence > claimAudioToTextGRE.confidence) {
      finaltext = claimAudioToTextSWE.text
      finalConfidence = claimAudioToTextSWE.confidence
    } else {
      finaltext = claimAudioToTextGRE.text
      finalConfidence = claimAudioToTextGRE.confidence
    }
    return ResponseEntity.ok(finaltext + finalConfidence)
  }
}
