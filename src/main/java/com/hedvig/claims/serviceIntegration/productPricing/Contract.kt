package com.hedvig.claims.serviceIntegration.productPricing

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Contract(
    val id: UUID,
    @get:JsonProperty("isTerminated")
    val isTerminated: Boolean
)
