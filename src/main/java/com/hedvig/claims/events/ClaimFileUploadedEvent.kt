package com.hedvig.claims.events

import java.util.*

data class ClaimFileUploadedEvent (
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
        val userId: String
)


