package com.hedvig.claims.query

import com.hedvig.claims.commands.AddDataItemCommand
import com.hedvig.claims.events.BackofficeClaimCreatedEvent
import com.hedvig.claims.events.ClaimCreatedEvent
import com.hedvig.claims.web.dto.ClaimDataType
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
@ProcessingGroup("BackFillDateOfClaim")
class BackFillDateOfClaimListener @Autowired constructor(
    private val claimRepository: ClaimsRepository,
    private val commandGateway: CommandGateway
) {

    @EventHandler
    fun on(e: BackofficeClaimCreatedEvent) {
        backfillDateOfClaim(e.id)
    }

    @EventHandler
    fun on(e: ClaimCreatedEvent) {
        backfillDateOfClaim(e.id)
    }

    private fun backfillDateOfClaim(claimId: String) {
        val claimEntity = claimRepository.findByIdOrNull(claimId) ?: return

        if (claimEntity.registrationDate == null || claimEntity.type == null) {
            return
        }

        if (claimEntity.data.any { dataItem -> dataItem.type == ClaimDataType.DataType.DATE }) {
            return
        }

        commandGateway.send<Void>(
            AddDataItemCommand(
                UUID.randomUUID().toString(),
                claimId,
                LocalDateTime.now(),
                claimEntity.userId,
                ClaimDataType.DataType.DATE,
                ClaimDataType.DataType.DATE.name,
                "Date",
                null,
                claimEntity.registrationDate.toString().replace("Z", "")
            )
        )
    }
}
