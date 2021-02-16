package com.hedvig.claims.payments

import com.hedvig.claims.commands.AddAutomaticPaymentCommand
import com.hedvig.claims.commands.AddExpensePaymentCommand
import com.hedvig.claims.commands.AddIndemnityCostPaymentCommand
import com.hedvig.claims.commands.AddNoteCommand
import com.hedvig.claims.commands.AddPaymentCommand
import com.hedvig.claims.query.ClaimsRepository
import com.hedvig.claims.serviceIntegration.meerkat.Meerkat
import com.hedvig.claims.serviceIntegration.meerkat.dto.SanctionStatus
import com.hedvig.claims.serviceIntegration.memberService.MemberService
import com.hedvig.claims.util.CreatePaymentOutcome
import com.hedvig.claims.web.dto.CreatePaymentDto
import com.hedvig.claims.web.dto.PaymentType
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ClaimPaymentService(
    private val claimsRepository: ClaimsRepository,
    private val commandBus: CommandGateway,
    private val meerkat: Meerkat,
    private val memberService: MemberService
) {

    fun createPayment(createPayment: CreatePaymentDto): CreatePaymentOutcome {
        return when (createPayment.type) {
            PaymentType.Automatic -> createAutomaticPaymentCommand(createPayment)
            PaymentType.Manual -> createManualPaymentCommand(createPayment)
            PaymentType.IndemnityCost -> createIndemnityCostPaymentCommand(createPayment)
            PaymentType.Expense -> createExpensePaymentCommand(createPayment)
        }
    }

    private fun createAutomaticPaymentCommand(request: CreatePaymentDto): CreatePaymentOutcome {
        val claim = claimsRepository.findByIdOrNull(request.claimId) ?: return CreatePaymentOutcome.CLAIM_NOT_FOUND
        val (memberId, firstName, lastName) = memberService.getMember(claim.userId)
            ?: return CreatePaymentOutcome.MEMBER_NOT_FOUND

        val memberStatus: SanctionStatus = meerkat
            .getMemberSanctionStatus("$firstName $lastName")

        if (memberStatus == SanctionStatus.FullHit ||
            request.note.trim().length < 5
        ) {
            return CreatePaymentOutcome.FORBIDDEN
        }

        if (!request.sanctionListSkipped && (memberStatus == SanctionStatus.Undetermined || memberStatus == SanctionStatus.PartialHit)) {
            return CreatePaymentOutcome.FORBIDDEN
        }

        commandBus.sendAndWait<Void>(
            AddNoteCommand(
                UUID.randomUUID().toString(),
                request.claimId,
                LocalDateTime.now(),
                request.note,
                memberId,
                claim.audioURL
            )
        )

        commandBus.sendAndWait<Void>(
            AddAutomaticPaymentCommand(
                request.claimId,
                memberId,
                request.amount,
                request.deductible,
                request.note,
                request.exGratia,
                request.handlerReference,
                request.sanctionListSkipped
            )
        )

        return CreatePaymentOutcome.COMPLETED
    }

    private fun createManualPaymentCommand(createPaymentDto: CreatePaymentDto): CreatePaymentOutcome {
        commandBus.sendAndWait<Void>(
            AddPaymentCommand(
                UUID.randomUUID().toString(),
                createPaymentDto.claimId,
                createPaymentDto.amount.number.doubleValueExact(),
                createPaymentDto.deductible.number.doubleValueExact(),
                createPaymentDto.note,
                createPaymentDto.exGratia,
                createPaymentDto.handlerReference
            )
        )
        return CreatePaymentOutcome.COMPLETED
    }

    private fun createIndemnityCostPaymentCommand(createPaymentDto: CreatePaymentDto): CreatePaymentOutcome {

        commandBus.sendAndWait<Void>(
            AddIndemnityCostPaymentCommand(
                UUID.randomUUID().toString(),
                createPaymentDto.claimId,
                LocalDateTime.now(),
                createPaymentDto.amount,
                createPaymentDto.deductible,
                createPaymentDto.note,
                createPaymentDto.exGratia,
                createPaymentDto.handlerReference
            )
        )
        return CreatePaymentOutcome.COMPLETED
    }

    private fun createExpensePaymentCommand(createPaymentDto: CreatePaymentDto): CreatePaymentOutcome {
        commandBus.sendAndWait<Void>(
            AddExpensePaymentCommand(
                UUID.randomUUID().toString(),
                createPaymentDto.claimId,
                LocalDateTime.now(),
                createPaymentDto.amount,
                createPaymentDto.deductible,
                createPaymentDto.note,
                createPaymentDto.exGratia,
                createPaymentDto.handlerReference
            )
        )
        return CreatePaymentOutcome.COMPLETED
    }
}
