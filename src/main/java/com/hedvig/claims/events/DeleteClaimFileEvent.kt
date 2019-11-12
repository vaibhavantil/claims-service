package com.hedvig.claims.events

import java.util.*

data class DeleteClaimFileEvent (
        val claimFileId: UUID,
        val deletedBy: String
)
