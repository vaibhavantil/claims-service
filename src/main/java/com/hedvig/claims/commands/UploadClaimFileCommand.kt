package com.hedvig.claims.commands

import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

data class UploadClaimFileCommand (
        val id: Long,
        val bucket: String,
        @TargetAggregateIdentifier
        val claimId: UUID,
        val contentType: String,
        val data: ByteArray,
        val fileName: String,
        val imageId: UUID,
        val metaInfo: String,
        val size: Long,
        val userId: String
)
