package com.hedvig.claims.serviceIntegration.productPricing

enum class ContractStatus {
    PENDING,
    ACTIVE_IN_FUTURE,
    ACTIVE_IN_FUTURE_AND_TERMINATED_IN_FUTURE,
    ACTIVE,
    TERMINATED_TODAY,
    TERMINATED_IN_FUTURE,
    TERMINATED;
}
