package com.hedvig.claims.web.dto

import java.util.stream.Stream

data class ReportDTO(
    val claims: Stream<ClaimReportDTO>,
    val numberOfSettledClaims: Int,
    val numberOfOpenedClaims: Int,
    val numberOfRejectedClaims: Int

)
