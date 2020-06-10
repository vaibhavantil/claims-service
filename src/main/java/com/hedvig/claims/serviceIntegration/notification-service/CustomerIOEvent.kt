package com.hedvig.claims.serviceIntegration.`notification-service`

data class CustomerIOEvent(
    val name: String,
    val data: Map<String, Any>
)
