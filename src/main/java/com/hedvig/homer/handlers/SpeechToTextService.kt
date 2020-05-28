package com.hedvig.homer.handlers

import java.util.UUID

interface SpeechToTextService {
  fun convertSpeechToText(audioURL: String, requestId: String): SpeechToTextResult
}
