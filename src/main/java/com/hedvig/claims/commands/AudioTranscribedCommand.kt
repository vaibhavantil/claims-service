package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class AudioTranscribedCommand(
    @TargetAggregateIdentifier
    val claimId: String,
    val text: String,
    val confidence: Float,
    val languageCode: String
)

