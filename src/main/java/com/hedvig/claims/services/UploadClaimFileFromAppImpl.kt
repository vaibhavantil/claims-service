package com.hedvig.claims.services

import com.amazonaws.services.s3.AmazonS3
import com.hedvig.claims.aggregates.ClaimsAggregate
import com.hedvig.claims.commands.UploadClaimFileCommand
import com.hedvig.claims.query.ClaimsRepository
import com.hedvig.claims.query.UploadSource
import com.hedvig.claims.web.dto.ClaimFileFromAppDTO
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.time.Instant
import java.util.*

@Component
class UploadClaimFileFromAppImpl @Autowired constructor(
    @Value("\${hedvig.chat}") val chatS3Bucket: String,
    @Value("\${hedvig.claims.filesUploaded}") val claimsS3Bucket: String,
    private val amazonS3: AmazonS3,
    private val claimsRepository: ClaimsRepository,
    private val commandBus: CommandGateway

    ): UploadClaimFileFromAppService {
    override fun copyFromAppUploadsS3BucketToClaimsS3Bucket(
        dto: ClaimFileFromAppDTO
    ) {

        val claims = claimsRepository.findByUserId(dto.memberId)

        val openClaims = claims.filter { claim -> claim.state == ClaimsAggregate.ClaimStates.OPEN || claim.state == ClaimsAggregate.ClaimStates.REOPENED }

        if (openClaims.size != 1) {
            throw RuntimeException("Either none or more than 1 claim was found for member ${dto.memberId}, we cannot know which claim to upload the file to")
        }

        val claimId = openClaims.last().id
        val key = "claim-$claimId/${UUID.randomUUID()}-uploadFromApp-${Math.random()}"

        amazonS3.copyObject(
            chatS3Bucket,
            dto.fileUploadKey,
            claimsS3Bucket,
            key
        )

        commandBus.sendAndWait<UploadClaimFileCommand>(
            UploadClaimFileCommand(
                UUID.randomUUID(),
                claimsS3Bucket,
                key,
                claimId,
                dto.mimeType,
                Instant.now(),
                "claim file uploaded from app",
                UploadSource.APP
            )
        )
    }
}
