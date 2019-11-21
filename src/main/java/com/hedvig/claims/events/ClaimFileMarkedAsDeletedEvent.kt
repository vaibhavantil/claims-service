package com.hedvig.claims.events

import java.time.Instant
import java.util.*

data class ClaimFileMarkedAsDeletedEvent (
        val claimFileId: UUID,
        val claimId: String,
        val deletedBy: String,
        val deletedAt: Instant
)
