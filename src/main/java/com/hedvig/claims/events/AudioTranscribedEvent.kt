package com.hedvig.claims.events

data class AudioTranscribedEvent(
    val claimId: String,
    val text: String,
    val confidence: Float)
