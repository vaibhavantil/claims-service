package com.hedvig.claims.services

import com.hedvig.claims.serviceIntegration.productPricing.Contract
import com.hedvig.claims.serviceIntegration.productPricing.ContractStatus
import com.hedvig.claims.serviceIntegration.productPricing.ProductPricingService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ProductPricingFacadeImpl(
    val productPricingService: ProductPricingService
): ProductPricingFacade {
    override fun getActiveContractAtTimeOfClaim(claimRegistrationDate: LocalDate, memberId: String): List<Contract> {
        val activeContractStates = listOf(ContractStatus.ACTIVE, ContractStatus.TERMINATED_TODAY, ContractStatus.TERMINATED_IN_FUTURE)

        return productPricingService.getContractsByMemberId(memberId).filter { contract ->
            activeContractStates.contains(contract.contractStatus)
        }

//        return productPricingService.getContractsByMemberId(memberId).filter { contract ->
//            !contract.masterInception!!.isAfter(claimRegistrationDate) &&
//                (!contract.isTerminated || !contract.terminationDate!!.isBefore(claimRegistrationDate))
//        }
    }
}
