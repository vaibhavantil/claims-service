package com.hedvig.homer.web

import com.hedvig.homer.handlers.SpeechToTextServiceImpl
import com.hedvig.homer.handlers.SpeechToTextResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class HomerController(
  val speechToTextService: SpeechToTextServiceImpl
) {
  @RequestMapping(
    path = ["/testSpeechToText"],
    method = [RequestMethod.POST]
  )
  fun test(@RequestBody aurioUrl: String): ResponseEntity<*>? {
    val result: SpeechToTextResult =
      speechToTextService.convertSpeechToText(aurioUrl)
    return ResponseEntity.ok(result.text + result.confidence)
  }
}
