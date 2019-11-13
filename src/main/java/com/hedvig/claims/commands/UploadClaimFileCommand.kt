package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.Instant
import java.util.*

data class UploadClaimFileCommand (
        val claimFileId: String,
        val bucket: String,
        val key: String,
        @TargetAggregateIdentifier
        val claimId: String,
        val contentType: String,
        val data: ByteArray,
        val fileName: String,
        val imageId: UUID,
        val metaInfo: String,
        val size: Long,
        val userId: String,
        val markedAsDeleted: Boolean? = false,
        val markedAsDeletedBy: String?,
        val markedAsDeletedAt: Instant?
)
