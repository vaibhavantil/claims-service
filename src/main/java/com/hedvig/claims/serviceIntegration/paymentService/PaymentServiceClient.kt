package com.hedvig.claims.serviceIntegration.paymentService

import com.hedvig.claims.serviceIntegration.paymentService.dto.PayoutRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@FeignClient(value = "payment-service", url = "\${hedvig.payment-service.url:payment-service}")
interface PaymentServiceClient {
    @PostMapping("/v2/_/members/{memberId}/payout")
    fun executePayment(
        @PathVariable(name = "memberId") memberId: String,
        @RequestBody request: PayoutRequest
    ): ResponseEntity<UUID>
}
