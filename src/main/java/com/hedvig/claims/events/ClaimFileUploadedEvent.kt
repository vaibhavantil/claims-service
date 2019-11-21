package com.hedvig.claims.events

import java.time.Instant
import java.util.*

data class ClaimFileUploadedEvent (
        val claimFileId: UUID,
        val bucket: String,
        val key: String,
        val claimId: String,
        val contentType: String,
        val uploadedAt: Instant,
        val fileName: String
)


