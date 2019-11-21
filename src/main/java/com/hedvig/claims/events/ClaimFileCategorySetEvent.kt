package com.hedvig.claims.events

import java.util.UUID

data class ClaimFileCategorySetEvent(
    val claimFileId: UUID,
    val category: String
)
