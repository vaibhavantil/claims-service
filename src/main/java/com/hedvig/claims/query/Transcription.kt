package com.hedvig.claims.query

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Transcription(
    @Id
    var id: String = UUID.randomUUID().toString(),
    @Column(columnDefinition = "TEXT")
    var text: String,
    var confidenceScore: Float,
    var languageCode: String
)
