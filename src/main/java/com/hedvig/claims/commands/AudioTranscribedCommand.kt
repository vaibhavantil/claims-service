package com.hedvig.claims.commands

data class AudioTranscribedCommand(
    val text:String,
    val confidence: Float
)

