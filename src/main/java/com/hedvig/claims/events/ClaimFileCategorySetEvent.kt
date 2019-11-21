package com.hedvig.claims.events

import java.util.*

data class ClaimFileCategorySetEvent (
    val claimFileId: UUID,
    val category: String
)
