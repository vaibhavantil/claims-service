package com.hedvig.claims.web.dto

import java.time.LocalDate
import java.time.Year
import java.util.*
import javax.money.MonetaryAmount

data class ClaimReportDTO(
    val claimId: UUID,
    val memberId: String,
    val dateOfLoss: LocalDate,
    val notificationDate: LocalDate,
    val claimYear: Year,
    val descriptionOfLoss: String,
    val grossPaid: MonetaryAmount,
    val reserved: MonetaryAmount,
    val totalIncurred: MonetaryAmount,
    val claimStatus: String,
    val claimStatusLastUpdated: LocalDate
)
