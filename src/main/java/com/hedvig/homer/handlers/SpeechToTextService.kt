package com.hedvig.homer.handlers

interface SpeechToTextService {
  fun convertSpeechToText(audioURL: String): SpeechToTextResult
}
