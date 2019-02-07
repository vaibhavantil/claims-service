package com.hedvig.claims.query

import java.math.BigDecimal
import java.time.LocalDate
import java.time.Year
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ClaimReportEntity(
    @Id
    val claimId: String,
    val memberId: String,
    val dateOfLoss: LocalDate?,
    val notificationDate: LocalDate,
    val claimYear: Year?,
    val descriptionOfLoss: String?,
    val grossPaid: BigDecimal?,
    val reserved: BigDecimal?,
    val totalIncurred: BigDecimal?,
    val currency: String?,
    val claimStatus: String,
    val claimStatusLastUpdated: LocalDate
) {
    constructor(
        claimId: String,
        memberId: String,
        notificationDate: LocalDate,
        claimStatus: String,
        claimStatusLastUpdated: LocalDate
    ) :
            this(
                claimId,
                memberId,
                null,
                notificationDate,
                null,
                null,
                null,
                null,
                null,
                null,
                claimStatus,
                claimStatusLastUpdated
            )
}
