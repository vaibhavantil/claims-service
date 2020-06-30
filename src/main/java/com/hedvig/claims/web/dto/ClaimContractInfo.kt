package com.hedvig.claims.web.dto

import java.util.*

data class ClaimContractInfo(
    val memberId: String,
    val claimId: String,
    val contractId: UUID
)
