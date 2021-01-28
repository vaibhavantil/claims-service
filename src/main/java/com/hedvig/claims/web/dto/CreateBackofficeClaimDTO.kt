package com.hedvig.claims.web.dto

import com.hedvig.claims.aggregates.ClaimSource
import java.time.Instant

data class CreateBackofficeClaimDTO(
    val memberId: String,
    val registrationDate: Instant,
    val claimSource: ClaimSource
)
