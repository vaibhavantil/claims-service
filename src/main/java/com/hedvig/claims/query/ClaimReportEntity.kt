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
    var grossPaid: BigDecimal?,
    var reserved: BigDecimal?,
    var totalIncurred: BigDecimal?
) {

    lateinit var dateOfLoss: LocalDate
    lateinit var notificationDate: LocalDate
    lateinit var claimYear: Year
    lateinit var descriptionOfLoss: String
    lateinit var currency: String
    lateinit var claimStatus: String
    lateinit var claimStatusLastUpdated: LocalDate

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
                null,
                null
            ) {
        this.notificationDate = notificationDate
        this.claimStatus = claimStatus
        this.claimStatusLastUpdated = claimStatusLastUpdated
    }

}
