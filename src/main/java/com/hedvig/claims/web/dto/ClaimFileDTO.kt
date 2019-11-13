package com.hedvig.claims.web.dto
import java.time.Instant
import java.util.*

data class ClaimFileDTO (
        val claimFileId: String,
        val bucket: String,
        val key: String,
        val claimId: String,
        val contentType: String,
        val data: ByteArray,
        val fileName: String,
        val imageId: UUID,
        val metaInfo: String,
        val size: Long,
        val userId: String?,
        val markedAsDeleted: Boolean,
        val markedAsDeletedBy: String?,
        val markedAsDeletedAt: Instant?
)


