package com.hedvig.homer.handlers

class SpeechToTextResult(
    val text: String,
    val confidence: Float,
    val languageCode: String,
    val alternatives: List<String>
)
