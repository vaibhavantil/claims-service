package com.hedvig.claims.services

import com.hedvig.claims.serviceIntegration.productPricing.Contract

interface ProductPricingFacade {
    fun getActiveContracts(memberId: String): List<Contract>
}
