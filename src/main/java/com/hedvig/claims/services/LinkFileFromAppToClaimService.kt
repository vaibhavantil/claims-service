package com.hedvig.claims.services

import com.hedvig.claims.web.dto.ClaimFileFromAppDTO

interface LinkFileFromAppToClaimService {
    fun copyFromAppUploadsS3BucketToClaimsS3Bucket(
        dto: ClaimFileFromAppDTO
    )
}
