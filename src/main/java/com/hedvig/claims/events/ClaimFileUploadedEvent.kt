package com.hedvig.claims.events

import com.hedvig.claims.query.UploadSource
import org.axonframework.serialization.Revision
import java.time.Instant
import java.util.UUID

@Revision("1.0")
data class ClaimFileUploadedEvent(
    val claimFileId: UUID,
    val bucket: String,
    val key: String,
    val claimId: String,
    val contentType: String,
    val uploadedAt: Instant,
    val fileName: String,
    val uploadSource: UploadSource
)
