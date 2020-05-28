package com.hedvig.homer

import com.hedvig.homer.handlers.SpeechToTextResult
import java.util.UUID

interface SpeechToTextService {
  fun convertSpeechToText(audioURL: String, requestId: String): SpeechToTextResult
}
