package com.hedvig.claims.web.dto

data class ClaimTranscriptions(
    val text: String,
    val confidenceScore: Float,
    val languageCode: String
)
