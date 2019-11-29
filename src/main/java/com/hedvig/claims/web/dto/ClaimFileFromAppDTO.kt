package com.hedvig.claims.web.dto

data class ClaimFileFromAppDTO(
    val fileUploadKey: String,
    val mimeType: String,
    val memberId: String
)
