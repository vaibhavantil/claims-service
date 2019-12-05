package com.hedvig.claims.services

import com.hedvig.claims.web.dto.ClaimFileFromAppDTO

interface LinkFileToClaimService {
    fun copyFromAppUploadsS3BucketToClaimsS3Bucket(
        dto: ClaimFileFromAppDTO
    )
}
