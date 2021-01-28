package com.hedvig.claims.web.dto

import java.time.LocalDateTime

data class StartClaimAudioDTO(
    val userId: String,
    val registrationDate: LocalDateTime,
    val audioURL: String
)
