package com.hedvig.homer

import com.hedvig.homer.handlers.SpeechToTextResult

interface SpeechToTextService {
  fun convertSpeechToText(audioURL: String, requestId: String): SpeechToTextResult
}
