package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.Instant
import java.util.*

data class UploadClaimFileCommand (
        val claimFileId: UUID,
        val bucket: String,
        val key: String,
        @TargetAggregateIdentifier
        val claimId: String,
        val contentType: String,
        val uploadedAt: Instant,
        val fileName: String,
        val markedAsDeleted: Boolean? = false,
        val markedAsDeletedBy: String?,
        val markedAsDeletedAt: Instant?
)
