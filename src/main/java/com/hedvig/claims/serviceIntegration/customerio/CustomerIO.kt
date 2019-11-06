package com.hedvig.claims.serviceIntegration.customerio

import com.hedvig.claims.aggregates.ClaimsAggregate
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("customer.io")
@ConditionalOnProperty(value = ["customerio.siteId", "customerio.apiKey"], matchIfMissing = false)
@Component
@EnableFeignClients
class CustomerIO(
    private val customerIOClient: CustomerIOClient
){
    val logger = LoggerFactory.getLogger(CustomerIO::class.java)

    fun setClaimStatus(userId: String, claimStatus: ClaimsAggregate.ClaimStates) {
        val traits = mapOf(
            "latest_claim_status" to claimStatus.name
        )
        try {
            this.customerIOClient.put(userId, traits)
        } catch (exception: Exception) {
            logger.error("Could not set \"latest_claim_status\" for $userId to $claimStatus in customer.io (exception=$exception)")
        }
    }
}
