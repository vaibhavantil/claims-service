package com.hedvig.homer.repository

import javax.annotation.Nullable
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class ClaimTranscription (
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0
) {
    @Nullable
    var claimId: String? = null

    @OneToMany(cascade = [(CascadeType.ALL)])
    var alternatives_list: MutableList<ClaimTranscriptionAlternative> = arrayListOf()

    @Nullable
    @Column(columnDefinition = "TEXT")
    var bestTranscript: String? = null

    @Nullable
    var confidenceScore: Float? = null

    @Nullable
    var languageCode: String? = null
}
