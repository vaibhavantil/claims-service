package com.hedvig.claims.services

import com.hedvig.claims.serviceIntegration.productPricing.Contract
import java.time.LocalDate

interface ProductPricingFacade {
    fun getActiveContractAtTimeOfClaim(claimRegistrationDate: LocalDate, memberId: String): List<Contract>
}
