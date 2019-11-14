package com.hedvig.claims.events

import java.time.Instant

data class MarkClaimFileAsDeletedEvent (
        val claimFileId: String,
        val deletedBy: String,
        val deletedAt: Instant
)
