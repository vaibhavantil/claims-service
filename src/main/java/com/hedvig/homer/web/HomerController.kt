package com.hedvig.homer.web

import com.hedvig.homer.handlers.SpeechToTextResult
import com.hedvig.homer.handlers.SpeechToTextService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class HomerController(
  val speechToTextService: SpeechToTextService
) {
  @RequestMapping(
    path = ["/testSpeechToText"],
    method = [RequestMethod.POST]
  )
  fun test(@RequestBody aurioUrl: String): ResponseEntity<*>? {
    val result: SpeechToTextResult =
      speechToTextService.convertSpeechToText(aurioUrl, UUID.randomUUID().toString())
    return ResponseEntity.ok(result.text + result.confidence)
  }
}
