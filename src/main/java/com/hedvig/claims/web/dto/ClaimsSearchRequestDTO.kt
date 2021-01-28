package com.hedvig.claims.web.dto

import org.springframework.data.domain.Sort

data class ClaimsSearchRequestDTO(
    val page: Int?,
    val pageSize: Int?,
    val sortBy: ClaimSortColumn?,
    val sortDirection: Sort.Direction?
)
