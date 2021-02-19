package com.hedvig.claims.serviceIntegration.predictor

interface Predictor {
    fun predictIfItsAccidentClaim(input: String): Boolean
}
