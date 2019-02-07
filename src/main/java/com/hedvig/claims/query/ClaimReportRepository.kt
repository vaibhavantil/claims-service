package com.hedvig.claims.query

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ClaimReportRepository : JpaRepository<ClaimReportEntity, UUID> {
}
