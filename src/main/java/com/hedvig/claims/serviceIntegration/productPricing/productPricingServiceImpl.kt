package com.hedvig.claims.serviceIntegration.productPricing

import org.springframework.stereotype.Service

@Service
class ProductPricingServiceImpl(
    private val productPricingClient: ProductPricingClient
): ProductPricingService {
    override fun getContractsByMemberId(memberId: String): List<Contract> {
        return productPricingClient.getContractsByMemberId(memberId).body
    }
}
