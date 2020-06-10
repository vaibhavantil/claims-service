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

    @PostMapping("_/customerio/{userId}/events")
    fun postUserEvent(@PathVariable userId: String, @RequestBody event: CustomerIOEvent): ResponseEntity<String>
}
