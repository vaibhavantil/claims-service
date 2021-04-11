package com.hedvig.homer.repository

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class ClaimTranscriptionAlternative(
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
	val claimTranscriptionAlternativeId: Long = 0
) {
	@Column(columnDefinition = "TEXT")
	var transcript: String? = null

	companion object {
		fun from(dto: String): ClaimTranscriptionAlternative {
			val temp = ClaimTranscriptionAlternative()
			temp.transcript = dto
			return temp
		}
	}
}
