package com.hedvig.claims.events

data class ClaimFileCategorySetEvent (
    val claimFileId: String,
    val category: String
)
