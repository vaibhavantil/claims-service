package com.hedvig.claims.commands

import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus
import org.axonframework.commandhandling.TargetAggregateIdentifier

data class AddFailedAutomaticPaymentCommand(
    val id: String,
    @TargetAggregateIdentifier
    val claimId: String,
    val memberId: String,
    val transactionStatus: TransactionStatus
)
