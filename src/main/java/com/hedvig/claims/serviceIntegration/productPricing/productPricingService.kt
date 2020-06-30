package com.hedvig.claims.serviceIntegration.productPricing

interface ProductPricingService {
    fun getContractsByMemberId(memberId: String): List<Contract>
}
