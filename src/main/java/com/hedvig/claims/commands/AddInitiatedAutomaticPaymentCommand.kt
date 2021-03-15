package com.hedvig.claims.commands

import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.UUID

data class AddInitiatedAutomaticPaymentCommand(
    val id: String,
    @TargetAggregateIdentifier
    val claimId: String,
    val memberId: String,
    val transactionReference: UUID,
    val transactionStatus: TransactionStatus
)
