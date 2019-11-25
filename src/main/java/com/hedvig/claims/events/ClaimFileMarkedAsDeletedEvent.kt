package com.hedvig.claims.events

import java.time.Instant
import java.util.UUID

data class ClaimFileMarkedAsDeletedEvent(
    val claimFileId: UUID,
    val claimId: String,
    val deletedBy: String,
    val deletedAt: Instant
)
