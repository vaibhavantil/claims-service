package com.hedvig.claims.serviceIntegration.customerio

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "customer.io.client",
    url = "\${customerio.url:https://track.customer.io/api}",
    configuration = [CustomerIOFeignConfiguration::class]
)
interface CustomerIOClient {
    @PostMapping("/v1/customers/{userId}/events")
    fun postUserEvent(
        @PathVariable userId: String,
        @RequestBody event: CustomerIOEvent
    )
}
