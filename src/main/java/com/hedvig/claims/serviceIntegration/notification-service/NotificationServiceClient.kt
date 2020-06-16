package com.hedvig.claims.serviceIntegration.`notification-service`

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "notification-service",
    url = "\${hedvig.notification-service.url:https://track.customer.io/api}"
)
interface NotificationServiceClient  {

    @PostMapping("_/customerio/{memberId}/events")
    fun postUserEvent(@PathVariable memberId: String, @RequestBody event: CustomerIOEvent): ResponseEntity<String>
}
