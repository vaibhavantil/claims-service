package com.hedvig.claims.aaafindahomeforme

import com.hedvig.claims.commands.AddAutomaticPaymentCommand
import com.hedvig.claims.commands.AddExpensePaymentCommand
import com.hedvig.claims.commands.AddIndemnityCostPaymentCommand
import com.hedvig.claims.commands.AddNoteCommand
import com.hedvig.claims.commands.AddPaymentCommand
import com.hedvig.claims.query.ClaimsRepository
import com.hedvig.claims.serviceIntegration.meerkat.Meerkat
import com.hedvig.claims.serviceIntegration.meerkat.dto.SanctionStatus
import com.hedvig.claims.serviceIntegration.memberService.MemberService
import com.hedvig.claims.web.dto.PaymentType
import com.hedvig.claims.web.dto.CreatePaymentDto
import java.time.LocalDateTime
import java.util.UUID
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class PaymentRequestRouter(
    private val claimsRepository: ClaimsRepository,
    private val commandBus: CommandGateway,
    private val meerkat: Meerkat,
    private val memberService: MemberService
) {

    fun routePayment(createPaymentDto: CreatePaymentDto): Results {
        return when (createPaymentDto.type) {
            PaymentType.Automatic -> createAutomaticPaymentCommand(createPaymentDto)
            PaymentType.Manual -> createManualPaymentCommand(createPaymentDto)
            PaymentType.IndemnityCost -> createIndemnityCostPaymentCommand(createPaymentDto)
            PaymentType.Expense -> createExpensePaymentCommand(createPaymentDto)
        }
    }

    private fun createAutomaticPaymentCommand(request: CreatePaymentDto): Results {
        val claim = claimsRepository.findByIdOrNull(request.claimId) ?: return Results.NOT_FOUND
        val (memberId, firstName, lastName) = memberService.getMember(claim.userId) ?: return Results.NOT_FOUND

        val memberStatus: SanctionStatus = meerkat
            .getMemberSanctionStatus(String.format("%s %s", firstName, lastName))

        if (memberStatus == SanctionStatus.FullHit
            || request.note.trim().length < 5
        ) {
            return Results.FORBIDDEN
        }

        val uid = UUID.randomUUID()
        val command = AddNoteCommand(
            uid.toString(),
            request.claimId,
            LocalDateTime.now(),
            request.note,
            memberId,
            claim.audioURL
        )
        commandBus.sendAndWait<Any>(command)

        val addAutomaticPaymentCommand = AddAutomaticPaymentCommand(
            request.claimId,
            memberId,
            request.amount,
            request.deductible,
            request.note,
            request.exGratia,
            request.handlerReference,
            request.sanctionCheckSkipped
        )

        commandBus.sendAndWait<Any>(addAutomaticPaymentCommand)

        return Results.ACCEPTED
    }

    enum class Results {
        ACCEPTED,
        FORBIDDEN,
        NO_CONTENT,
        NOT_FOUND,
    }

    private fun createManualPaymentCommand(createPaymentDto: CreatePaymentDto): Results {
        val command = AddPaymentCommand(
            UUID.randomUUID().toString(),
            createPaymentDto.claimId,
            LocalDateTime.now(),
            createPaymentDto.amount.number.doubleValueExact(),
            createPaymentDto.deductible.number.doubleValueExact(),
            createPaymentDto.note,
            createPaymentDto.exGratia,
            createPaymentDto.handlerReference
        )

        commandBus.sendAndWait<Any>(command)
        return Results.NO_CONTENT
    }

    private fun createIndemnityCostPaymentCommand(createPaymentDto: CreatePaymentDto): Results {
        val command = AddIndemnityCostPaymentCommand(
            UUID.randomUUID().toString(),
            createPaymentDto.claimId,
            LocalDateTime.now(),
            createPaymentDto.amount,
            createPaymentDto.deductible,
            createPaymentDto.note,
            createPaymentDto.exGratia,
            createPaymentDto.handlerReference
        )

        commandBus.sendAndWait<Any>(command)
        return Results.NO_CONTENT
    }

    private fun createExpensePaymentCommand(createPaymentDto: CreatePaymentDto): Results {
        val command = AddExpensePaymentCommand(
            UUID.randomUUID().toString(),
            createPaymentDto.claimId,
            LocalDateTime.now(),
            createPaymentDto.amount,
            createPaymentDto.deductible,
            createPaymentDto.note,
            createPaymentDto.exGratia,
            createPaymentDto.handlerReference
        )

        commandBus.sendAndWait<Void>(command)
        return Results.NO_CONTENT
    }
}
