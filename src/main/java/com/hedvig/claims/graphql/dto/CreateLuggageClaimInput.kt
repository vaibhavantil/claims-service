package com.hedvig.claims.graphql.dto

data class CreateLuggageClaimInput(
        val reference: String?,
        val from: String,
        val to: String,
        val hoursDelayed: Int
)
