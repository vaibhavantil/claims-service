package com.hedvig.claims.web

import com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates
import com.hedvig.claims.commands.AddAutomaticPaymentCommand
import com.hedvig.claims.commands.AddDataItemCommand
import com.hedvig.claims.commands.AddNoteCommand
import com.hedvig.claims.commands.AddPaymentCommand
import com.hedvig.claims.commands.CreateBackofficeClaimCommand
import com.hedvig.claims.commands.CreateClaimCommand
import com.hedvig.claims.commands.MarkClaimFileAsDeletedCommand
import com.hedvig.claims.commands.SetClaimFileCategoryCommand
import com.hedvig.claims.commands.SetContractForClaimCommand
import com.hedvig.claims.commands.UpdateClaimTypeCommand
import com.hedvig.claims.commands.UpdateClaimsReserveCommand
import com.hedvig.claims.commands.UpdateClaimsStateCommand
import com.hedvig.claims.commands.UpdateEmployeeClaimStatusCommand
import com.hedvig.claims.commands.UploadClaimFileCommand
import com.hedvig.claims.query.ClaimEntity
import com.hedvig.claims.query.ClaimFileRepository
import com.hedvig.claims.query.ClaimsRepository
import com.hedvig.claims.query.ResourceNotFoundException
import com.hedvig.claims.query.UploadSource
import com.hedvig.claims.serviceIntegration.meerkat.Meerkat
import com.hedvig.claims.serviceIntegration.meerkat.dto.SanctionStatus
import com.hedvig.claims.serviceIntegration.memberService.MemberService
import com.hedvig.claims.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.claims.services.ClaimsQueryService
import com.hedvig.claims.services.LinkFileToClaimService
import com.hedvig.claims.services.ProductPricingFacade
import com.hedvig.claims.web.dto.ActiveClaimsDTO
import com.hedvig.claims.web.dto.ClaimContractInfo
import com.hedvig.claims.web.dto.ClaimDTO
import com.hedvig.claims.web.dto.ClaimDataType
import com.hedvig.claims.web.dto.ClaimFileCategoryDTO
import com.hedvig.claims.web.dto.ClaimFileFromAppDTO
import com.hedvig.claims.web.dto.ClaimStateDTO
import com.hedvig.claims.web.dto.ClaimType
import com.hedvig.claims.web.dto.ClaimTypeDTO
import com.hedvig.claims.web.dto.ClaimsByIdsDTO
import com.hedvig.claims.web.dto.ClaimsFilesUploadDTO
import com.hedvig.claims.web.dto.ClaimsSearchRequestDTO
import com.hedvig.claims.web.dto.ClaimsSearchResultDTO
import com.hedvig.claims.web.dto.CreateBackofficeClaimDTO
import com.hedvig.claims.web.dto.CreateBackofficeClaimResponseDTO
import com.hedvig.claims.web.dto.DataItemDTO
import com.hedvig.claims.web.dto.EmployeeClaimRequestDTO
import com.hedvig.claims.web.dto.MarkClaimFileAsDeletedDTO
import com.hedvig.claims.web.dto.NoteDTO
import com.hedvig.claims.web.dto.PaymentDTO
import com.hedvig.claims.web.dto.PaymentRequestDTO
import com.hedvig.claims.web.dto.ReserveDTO
import com.hedvig.claims.web.dto.StartClaimAudioDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID
import java.util.stream.Stream

@RestController
@RequestMapping(value = ["/i/claims", "/_/claims"])
class InternalController(
    private val claimsRepository: ClaimsRepository,
    private val commandBus: CommandGateway,
    private val claimsQueryService: ClaimsQueryService,
    private val meerkat: Meerkat,
    private val memberService: MemberService,
    private val claimFileRepository: ClaimFileRepository,
    private val linkFileToClaimService: LinkFileToClaimService,
    private val productPricingService: ProductPricingService,
    private val productPricingFacade: ProductPricingFacade
) {

    private val log = LoggerFactory.getLogger(InternalController::class.java)

    @PostMapping("/startClaimFromAudio")
    fun initiateClaim(@RequestBody requestData: StartClaimAudioDTO): ResponseEntity<*> {
        val uuid = UUID.randomUUID()
        val activeContracts = productPricingFacade.getActiveContracts(requestData.userId)
        val command = CreateClaimCommand(
            uuid.toString(),
            requestData.userId,
            requestData.audioURL,
            if (activeContracts.size == 1) activeContracts[0].id else null
        )
        commandBus.sendAndWait<Any>(command)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
    }

    @PostMapping("/createFromBackOffice")
    fun createClaim(@RequestBody request: CreateBackofficeClaimDTO): ResponseEntity<*> {
        val uuid = UUID.randomUUID()
        val activeContracts = productPricingFacade.getActiveContracts(request.memberId)
        val command = CreateBackofficeClaimCommand(
            uuid.toString(),
            request.memberId,
            request.registrationDate,
            request.claimSource,
            if (activeContracts.size == 1) activeContracts[0].id else null
        )
        commandBus.sendAndWait<Any>(command)
        return ResponseEntity.ok(CreateBackofficeClaimResponseDTO(uuid))
    }

    @GetMapping("/listclaims")
    fun getClaimsList(): ResponseEntity<List<ClaimDTO>> {
        val claims = claimsRepository.findAll().map {
            ClaimDTO(
                it.id,
                it.userId,
                it.state,
                it.reserve,
                it.type,
                it.audioURL,
                it.registrationDate,
                it.claimSource,
                it.coveringEmployee
            )
        }
        return ResponseEntity.ok(claims)
    }

    @GetMapping("/search")
    fun search(req: ClaimsSearchRequestDTO): ClaimsSearchResultDTO {
        return claimsQueryService.search(req)
    }

    @GetMapping("/listclaims/{userId}")
    fun getClaimsByUserId(@PathVariable userId: String): List<ClaimDTO> {
        return claimsRepository
            .findByUserId(userId)
            .map { claim: ClaimEntity ->
                ClaimDTO(
                    claim.id,
                    claim.userId,
                    claim.state,
                    claim.reserve,
                    claim.type,
                    claim.audioURL,
                    claim.registrationDate,
                    claim.claimSource,
                    claim.coveringEmployee
                )
            }
    }

    @GetMapping("/activeClaims/{userId}")
    fun getActiveClaims(@PathVariable userId: String): ActiveClaimsDTO {
        val activeClaims = claimsRepository.findByUserId(userId)
            .count { c: ClaimEntity -> c.state == ClaimStates.OPEN }
        return ActiveClaimsDTO(activeClaims)
    }

    @GetMapping("/stat")
    fun claimsStatisticsByState(): Map<String, Long> {
        return ClaimStates.values().associateBy(
            { it.name },
            claimsRepository::countByState
        )
    }

    @GetMapping("/claim")
    fun getClaim(@RequestParam claimID: String): ResponseEntity<ClaimDTO> {
        val claim = claimsRepository.findById(claimID)
            .orElseThrow { ResourceNotFoundException("Could not find claim with id:$claimID") }
        return ResponseEntity.ok(ClaimDTO(claim))
    }

    @PostMapping("/adddataitem")
    fun addDataItem(@RequestBody data: DataItemDTO): ResponseEntity<*> {
        val uid = UUID.randomUUID()
        val command = AddDataItemCommand(
            uid.toString(),
            data.claimID,
            LocalDateTime.now(),
            data.userId,
            data.type,
            data.name,
            data.title,
            data.received,
            data.value
        )
        commandBus.sendAndWait<Any>(command)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
    }

    @PostMapping("/addnote")
    fun addNote(@RequestBody note: NoteDTO): ResponseEntity<*> {
        val uid = UUID.randomUUID()
        val command = AddNoteCommand(
            uid.toString(),
            note.claimID,
            LocalDateTime.now(),
            note.text,
            note.userId,
            note.fileURL
        )
        commandBus.sendAndWait<Any>(command)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
    }

    @PostMapping("/addpayment")
    fun addPayment(@RequestBody payment: PaymentDTO): ResponseEntity<*> {
        val command = AddPaymentCommand(
            UUID.randomUUID().toString(),
            payment.claimID,
            LocalDateTime.now(),
            payment.userId,
            payment.amount,
            payment.deductible,
            payment.note,
            payment.payoutDate,
            payment.exGratia,
            payment.handlerReference
        )
        commandBus.sendAndWait<Any>(command)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
    }

    @PostMapping("/{memberId}/addAutomaticPayment")
    fun addAutomaticPayment(
        @PathVariable memberId: String,
        @RequestBody request: PaymentRequestDTO
    ): ResponseEntity<*> {
        val member = memberService.getMember(memberId) ?: return ResponseEntity.notFound().build<Any>()

        val memberStatus = meerkat
            .getMemberSanctionStatus(String.format("%s %s", member.firstName, member.lastName))
        if (memberStatus == SanctionStatus.FullHit) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build<Any>()
        }

        if (!request.sanctionCheckSkipped
            && (memberStatus == SanctionStatus.Undetermined || memberStatus == SanctionStatus.PartialHit)
        ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build<Any>()
        }

        if (request.sanctionCheckSkipped) {
            val claim = claimsRepository.findByIdOrNull(request.claimId.toString())
                ?: return ResponseEntity.notFound().build<Any>()

            if (request.paymentRequestNote == null
                || request.paymentRequestNote.trim { it <= ' ' }.length < 5
            ) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build<Any>()
            }
            val uid = UUID.randomUUID()
            val command = AddNoteCommand(
                uid.toString(),
                request.claimId.toString(),
                LocalDateTime.now(),
                request.paymentRequestNote,
                memberId,
                claim.audioURL
            )
            commandBus.sendAndWait<Any>(command)
        }
        val addAutomaticPaymentCommand = AddAutomaticPaymentCommand(
            request.claimId.toString(),
            memberId,
            request.amount,
            request.deductible,
            request.paymentRequestNote,
            request.exGratia,
            request.handlerReference,
            request.sanctionCheckSkipped
        )
        commandBus.sendAndWait<Any>(addAutomaticPaymentCommand)

        return ResponseEntity.accepted().build<Any>()
    }

    @PostMapping("/updatereserve")
    fun updateReserve(@RequestBody reserve: ReserveDTO): ResponseEntity<*> {
        val command = UpdateClaimsReserveCommand(
            reserve.claimID,
            reserve.userId,
            LocalDateTime.now(),
            reserve.amount
        )
        commandBus.sendAndWait<Any>(command)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
    }

    @PostMapping("/updatestate")
    fun updateState(@RequestBody state: ClaimStateDTO): ResponseEntity<*> {
        val command = UpdateClaimsStateCommand(
            state.claimID,
            state.userId,
            LocalDateTime.now(),
            state.state
        )
        commandBus.sendAndWait<Any>(command)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
    }

    @PostMapping("/updatetype")
    fun updateType(@RequestBody type: ClaimTypeDTO): ResponseEntity<*> {
        val command = UpdateClaimTypeCommand(
            type.claimID,
            type.userId,
            LocalDateTime.now(),
            type.type
        )
        commandBus.sendAndWait<Any>(command)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
    }

    @PostMapping("/setContractForClaim")
    fun setContractForClaim(@RequestBody dto: ClaimContractInfo): ResponseEntity<Void> {
        val command = SetContractForClaimCommand(
            dto.claimId,
            dto.memberId,
            dto.contractId
        )
        commandBus.sendAndWait<Any>(command)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/backfillSetContractsForClaims")
    fun backfillSetContractsForClaims(): ResponseEntity<Void> {
        val claimsWithContractIdOfNull = claimsRepository.findClaimsWithContractIdOfNull()
        claimsWithContractIdOfNull.forEach { claim: ClaimEntity ->
            val contracts = productPricingService.getContractsByMemberId(claim.userId)
            if (contracts.size == 1) {
                val command = SetContractForClaimCommand(
                    claim.id,
                    claim.userId,
                    contracts[0].id
                )
                try {
                    commandBus.sendAndWait<Any>(command)
                } catch (e: Exception) {
                    log.error(
                        "Unable to automatically set contract to claim for (memberId={}, claimId={}))",
                        claim.userId,
                        claim.id,
                        e
                    )
                }
            } else if (contracts.isEmpty()) {
                log.error(
                    "Unable to automatically set contract to claim since no contracts are present (memberId={}, claimId={})",
                    claim.userId,
                    claim.id
                )
            } else {
                log.error(
                    "Unable to automatically set contract to claim since more than one contract is present (memberId={}, claimId={})",
                    claim.userId,
                    claim.id
                )
            }
        }
        return ResponseEntity.ok().build()
    }

    @GetMapping("claimTypes")
    fun claimTypes(): ResponseEntity<List<ClaimType>> {
        val claimTypes = mutableListOf<ClaimType>()
        val typeDate = ClaimDataType(ClaimDataType.DataType.DATE, "DATE", "Date")
        val typePlace = ClaimDataType(ClaimDataType.DataType.TEXT, "PLACE", "Place")
        val typeItem = ClaimDataType(ClaimDataType.DataType.ASSET, "ITEM", "Item")
        val typePoliceReport = ClaimDataType(
            ClaimDataType.DataType.FILE, "POLICE_REPORT",
            "Police report"
        )
        val typeReceipt = ClaimDataType(ClaimDataType.DataType.FILE, "RECEIPT", "Receipt")
        val typeTicket = ClaimDataType(ClaimDataType.DataType.TICKET, "TICKET", "Ticket")
        val ct1 = ClaimType("Theft - Other", "Theft - Other", false)
        ct1.addRequiredData(typeDate)
        ct1.addRequiredData(typePlace)
        ct1.addRequiredData(typeItem)
        ct1.addOptionalData(typePoliceReport)
        ct1.addOptionalData(typeReceipt)
        val ct2 = ClaimType("Theft - Home", "Theft - Home", false)
        ct2.addRequiredData(typeDate)
        ct2.addRequiredData(typePlace)
        ct2.addRequiredData(typeItem)
        ct2.addOptionalData(typePoliceReport)
        ct2.addOptionalData(typeReceipt)
        val ct3 = ClaimType("Theft - Bike", "Theft - Bike", false)
        ct3.addRequiredData(typeDate)
        ct3.addRequiredData(typePlace)
        ct3.addRequiredData(typeItem)
        ct3.addOptionalData(typePoliceReport)
        ct3.addOptionalData(typeReceipt)
        val ct4 = ClaimType("Assault", "Assault", false)
        ct4.addRequiredData(typeDate)
        ct4.addRequiredData(typePlace)
        ct4.addOptionalData(typePoliceReport)
        val ct5 = ClaimType("Drulle - Mobile", "Drulle - Mobile", false)
        ct5.addRequiredData(typeDate)
        ct5.addRequiredData(typePlace)
        ct5.addRequiredData(typeItem)
        ct5.addOptionalData(typePoliceReport)
        ct5.addOptionalData(typeReceipt)
        val ct6 = ClaimType("Drulle - Other", "Drulle - Other", false)
        ct6.addRequiredData(typeDate)
        ct6.addRequiredData(typePlace)
        ct6.addRequiredData(typeItem)
        ct6.addOptionalData(typePoliceReport)
        ct6.addOptionalData(typeReceipt)
        val ct7 = ClaimType("Water Damage - Kitchen", "Water Damage - Kitchen", false)
        ct7.addRequiredData(typeDate)
        val ct8 = ClaimType("Water Damage - Bathroom", "Water Damage - Bathroom", false)
        ct8.addRequiredData(typeDate)
        val ct9 = ClaimType(
            "Travel - Accident and Health", "Travel - Accident and Health",
            false
        )
        ct9.addRequiredData(typeDate)
        ct9.addRequiredData(typePlace)
        ct9.addOptionalData(typePoliceReport)
        ct9.addOptionalData(typeReceipt)
        val ct10 = ClaimType("Travel - Delayed Luggage", "Travel - Delayed Luggage", false)
        ct10.addRequiredData(typeDate)
        ct10.addRequiredData(typePlace)
        ct10.addOptionalData(typeTicket)
        val ct11 = ClaimType("Not covered", "Not covered", false)
        ct11.addRequiredData(typeDate)
        val ct12 = ClaimType("Confirmed Fraud", "Confirmed Fraud", false)
        ct12.addRequiredData(typeDate)
        val ct13 = ClaimType("Test", "Test", false)
        ct13.addRequiredData(typeDate)
        val ct14 = ClaimType("Liability", "Liability", false)
        ct14.addRequiredData(typeDate)
        ct14.addRequiredData(typePlace)
        val ct15 = ClaimType("Fire Damage", "Fire Damage", false)
        ct15.addRequiredData(typeDate)
        ct15.addRequiredData(typePlace)
        val ct16 = ClaimType("Appliance", "Appliance", false)
        ct16.addRequiredData(typeDate)
        ct16.addRequiredData(typePlace)
        ct16.addRequiredData(typeItem)
        claimTypes.add(ct1)
        claimTypes.add(ct2)
        claimTypes.add(ct3)
        claimTypes.add(ct4)
        claimTypes.add(ct5)
        claimTypes.add(ct6)
        claimTypes.add(ct7)
        claimTypes.add(ct8)
        claimTypes.add(ct9)
        claimTypes.add(ct10)
        claimTypes.add(ct11)
        claimTypes.add(ct12)
        claimTypes.add(ct13)
        claimTypes.add(ct14)

        // old claim types
        val c11 = ClaimDataType(ClaimDataType.DataType.DATE, "DATE", "Datum")
        val c12 = ClaimDataType(ClaimDataType.DataType.TEXT, "PLACE", "Plats")
        val c13 = ClaimDataType(ClaimDataType.DataType.ASSET, "ITEM", "Pryl")
        val c14 = ClaimDataType(ClaimDataType.DataType.FILE, "POLICE_REPORT", "Polisanmälan")
        val c15 = ClaimDataType(ClaimDataType.DataType.FILE, "RECIEPT", "Kvitto")
        val oldCt1 = ClaimType("THEFT", "Stöld", true)
        oldCt1.addRequiredData(c11)
        oldCt1.addRequiredData(c12)
        oldCt1.addRequiredData(c13)
        oldCt1.addOptionalData(c14)
        oldCt1.addOptionalData(c15)
        val oldCt2 = ClaimType("FIRE", "Brand", true)
        oldCt2.addRequiredData(c11)
        oldCt2.addRequiredData(c12)
        oldCt2.addRequiredData(c13)
        oldCt2.addOptionalData(c14)
        oldCt2.addOptionalData(c15)
        val oldCt3 = ClaimType("DRULLE", "Drulle", true)
        oldCt3.addRequiredData(c11)
        oldCt3.addRequiredData(c12)
        oldCt3.addRequiredData(c13)
        oldCt3.addOptionalData(c14)
        oldCt3.addOptionalData(c15)
        val oldCt4 = ClaimType("FILE", "Brand", true)
        oldCt4.addRequiredData(c11)
        oldCt4.addRequiredData(c12)
        oldCt4.addRequiredData(c13)
        oldCt4.addOptionalData(c14)
        oldCt4.addOptionalData(c15)
        claimTypes.add(oldCt1)
        claimTypes.add(oldCt2)
        claimTypes.add(oldCt3)
        claimTypes.add(oldCt4)
        return ResponseEntity.ok(claimTypes)
    }

    @PostMapping("/many")
    fun getClaimsByIds(@RequestBody dto: ClaimsByIdsDTO): ResponseEntity<Stream<ClaimDTO>> {
        val claims = claimsRepository.findAllById(dto.ids.map { it.toString() })
        if (claims.size != dto.ids.size) {
            log.error(
                "Length mismatch on supplied claims and found claims: wanted {}, found {}",
                dto.ids.size, claims.size
            )
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(claims.stream().map { claim: ClaimEntity -> ClaimDTO(claim) })
    }

    @PostMapping("/employee")
    fun markClaimAsEmployee(@RequestBody dto: EmployeeClaimRequestDTO): ResponseEntity<*> {
        claimsRepository.findByIdOrNull(dto.claimId) ?: return ResponseEntity.badRequest().build<Any>()
        commandBus.sendAndWait<Any>(UpdateEmployeeClaimStatusCommand(dto.claimId, dto.coveringEmployee))
        return ResponseEntity.accepted().build<Any>()
    }

    @PostMapping("claimFiles")
    fun link(@RequestBody dto: ClaimsFilesUploadDTO): ResponseEntity<Void> {
        dto.claimsFiles.forEach { (claimFileId, bucket, key, claimId, contentType, uploadedAt, fileName) ->
            commandBus.sendAndWait<Any>(
                UploadClaimFileCommand(
                    claimFileId,
                    bucket,
                    key,
                    claimId,
                    contentType,
                    uploadedAt,
                    fileName,
                    UploadSource.MANUAL
                )
            )
        }
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/linkFileToClaim")
    fun linkFileFromAppToClaim(@RequestBody dto: ClaimFileFromAppDTO?): ResponseEntity<Void> {
        linkFileToClaimService.copyFromAppUploadsS3BucketToClaimsS3Bucket(dto!!)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{claimId}/claimFile/{claimFileId}/delete")
    fun markClaimFileAsDeleted(
        @PathVariable claimId: String,
        @PathVariable claimFileId: UUID,
        @RequestBody dto: MarkClaimFileAsDeletedDTO
    ): ResponseEntity<Void> {
        claimFileRepository.findByIdOrNull(claimFileId)?.let {
            commandBus.sendAndWait<Any>(MarkClaimFileAsDeletedCommand(claimFileId, claimId!!, dto.deletedBy))
        }
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{claimId}/claimFile/{claimFileId}/setClaimFileCategory")
    fun setClaimFileCategory(
        @PathVariable claimId: String,
        @PathVariable claimFileId: UUID,
        @RequestBody dto: ClaimFileCategoryDTO
    ): ResponseEntity<Void> {
        claimFileRepository.findByIdOrNull(claimFileId)?.let {
            commandBus.sendAndWait<Any>(SetClaimFileCategoryCommand(claimFileId, claimId!!, dto.category))
        }
        return ResponseEntity.noContent().build()
    }
}
