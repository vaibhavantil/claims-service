package com.hedvig.claims.query

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ClaimsRepository : JpaRepository<ClaimEntity, String> {

    fun findByUserId(userId: String): List<ClaimEntity>

    fun countByState(state: ClaimStates): Long

    @Query("SELECT c FROM ClaimEntity c")
    fun search(p: Pageable?): Page<ClaimEntity>?

    @Query("SELECT c from ClaimEntity c where type = :type")
    fun findByType(type: String): List<ClaimEntity>

    @Query("FROM ClaimEntity ce WHERE ce.contractId IS NULL")
    fun findClaimsWithContractIdOfNull(): List<ClaimEntity>

    fun findAllByTranscriptionsIsNull() : List<ClaimEntity>
}
