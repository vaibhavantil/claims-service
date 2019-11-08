package com.hedvig.claims.serviceIntegration.customerio

data class CustomerIOEvent(
    val name: String,
    val data: Map<String, Any>
)
