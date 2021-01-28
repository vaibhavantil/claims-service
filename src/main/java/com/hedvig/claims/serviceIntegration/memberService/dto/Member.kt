package com.hedvig.claims.serviceIntegration.memberService.dto

import java.time.LocalDate

data class Member(
    val memberId: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val birthDate: LocalDate? = null,
    val street: String? = null,
    val city: String? = null,
    val zipCode: String? = null,
    val country: String? = null
)
