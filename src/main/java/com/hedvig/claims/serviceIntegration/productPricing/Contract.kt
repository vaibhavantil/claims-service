package com.hedvig.claims.serviceIntegration.productPricing

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.*

data class Contract(
    val id: UUID,
    val masterInception: LocalDate?,
    @get:JsonProperty("isTerminated")
    val isTerminated: Boolean,
    val terminationDate: LocalDate?,
    val contractStatus: ContractStatus
)
