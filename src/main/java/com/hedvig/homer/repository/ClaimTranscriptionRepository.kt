package com.hedvig.homer.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ClaimTranscriptionRepository : CrudRepository<ClaimTranscription, Long> {
}
