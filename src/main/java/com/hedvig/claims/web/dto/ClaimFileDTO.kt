package com.hedvig.claims.web.dto
import java.util.*

data class ClaimFileDTO (
        val id: Long,
        val bucket: String,
        val claimId: UUID,
        val contentType: String,
        val data: ByteArray,
        val fileName: String,
        val imageId: UUID,
        val metaInfo: String,
        val size: Long,
        val userId: String?
)


