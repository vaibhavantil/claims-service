package com.hedvig.claims.web;

import static com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates.OPEN;

import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.commands.AddAutomaticPaymentCommand;
import com.hedvig.claims.commands.AddDataItemCommand;
import com.hedvig.claims.commands.AddNoteCommand;
import com.hedvig.claims.commands.AddPaymentCommand;
import com.hedvig.claims.commands.CreateBackofficeClaimCommand;
import com.hedvig.claims.commands.CreateClaimCommand;
import com.hedvig.claims.commands.UpdateClaimTypeCommand;
import com.hedvig.claims.commands.UpdateClaimsReserveCommand;
import com.hedvig.claims.commands.UpdateClaimsStateCommand;
import com.hedvig.claims.query.ClaimEntity;
import com.hedvig.claims.query.ClaimsRepository;
import com.hedvig.claims.query.ResourceNotFoundException;
import com.hedvig.claims.serviceIntegration.meerkat.Meerkat;
import com.hedvig.claims.serviceIntegration.meerkat.dto.SanctionStatus;
import com.hedvig.claims.serviceIntegration.memberService.MemberService;
import com.hedvig.claims.serviceIntegration.memberService.dto.Member;
import com.hedvig.claims.services.ClaimsQueryService;
import com.hedvig.claims.web.dto.ActiveClaimsDTO;
import com.hedvig.claims.web.dto.ClaimDTO;
import com.hedvig.claims.web.dto.ClaimDataType;
import com.hedvig.claims.web.dto.ClaimDataType.DataType;
import com.hedvig.claims.web.dto.ClaimStateDTO;
import com.hedvig.claims.web.dto.ClaimType;
import com.hedvig.claims.web.dto.ClaimTypeDTO;
import com.hedvig.claims.web.dto.ClaimsByIdsDTO;
import com.hedvig.claims.web.dto.ClaimsSearchRequestDTO;
import com.hedvig.claims.web.dto.ClaimsSearchResultDTO;
import com.hedvig.claims.web.dto.CreateBackofficeClaimDTO;
import com.hedvig.claims.web.dto.CreateBackofficeClaimResponseDTO;
import com.hedvig.claims.web.dto.DataItemDTO;
import com.hedvig.claims.web.dto.NoteDTO;
import com.hedvig.claims.web.dto.PaymentDTO;
import com.hedvig.claims.web.dto.PaymentRequestDTO;
import com.hedvig.claims.web.dto.ReserveDTO;
import com.hedvig.claims.web.dto.StartClaimAudioDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import java.time.Instant;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/i/claims", "/_/claims"})
public class InternalController {

  private Logger log = LoggerFactory.getLogger(InternalController.class);
  private final ClaimsRepository claimsRepository;
  private final CommandGateway commandBus;
  private final ClaimsQueryService claimsQueryService;
  private final Meerkat meerkat;
  private final MemberService memberService;

  @Autowired
  public InternalController(CommandBus commandBus, ClaimsRepository repository,
    ClaimsQueryService claimsQueryService,
    Meerkat meerkat, MemberService memberService) {
    this.commandBus = new DefaultCommandGateway(commandBus);
    this.claimsRepository = repository;
    this.claimsQueryService = claimsQueryService;
    this.meerkat = meerkat;
    this.memberService = memberService;
  }

  @RequestMapping(path = "/startClaimFromAudio", method = RequestMethod.POST)
  public ResponseEntity<?> initiateClaim(@RequestBody StartClaimAudioDTO requestData) {
    log.info("Claim recieved!:" + requestData.toString());
    UUID uid = UUID.randomUUID();
    commandBus.sendAndWait(
        new CreateClaimCommand(
            uid.toString(),
            requestData.getUserId(),
            requestData.getAudioURL()));
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/createFromBackOffice", method = RequestMethod.POST)
  public ResponseEntity<?> createClaim(@RequestBody CreateBackofficeClaimDTO req) {
    log.info("Claim recieved!:" + req.toString());
    UUID uid = UUID.randomUUID();
    commandBus.sendAndWait(new CreateBackofficeClaimCommand(uid.toString(), req.getMemberId(),
      req.getRegistrationDate(), req.getClaimSource()));
    return ResponseEntity.ok(new CreateBackofficeClaimResponseDTO(uid));
  }

  @RequestMapping(path = "/listclaims", method = RequestMethod.GET)
  public ResponseEntity<List<ClaimDTO>> getClaimsList() {
    log.info("Getting all claims:");
    ArrayList<ClaimDTO> claims = new ArrayList<>();
    for (ClaimEntity c : claimsRepository.findAll()) {
      claims.add(
        new ClaimDTO(c.id, c.userId, c.state, c.reserve, c.type, c.audioURL, c.registrationDate,
          c.claimSource));
    }

    return ResponseEntity.ok(claims);
  }

  @RequestMapping(path = "/search", method = RequestMethod.GET)
  public ClaimsSearchResultDTO search(ClaimsSearchRequestDTO req) {
    log.info("Searching claims");
    ClaimsSearchResultDTO res = claimsQueryService.search(req);
    return res;
  }

  @RequestMapping(path = "/listclaims/{userId}", method = RequestMethod.GET)
  public List<ClaimDTO> getClaimsByUserId(@PathVariable String userId) {
    log.info("Getting claims for: {}", userId);
    return claimsRepository
      .findByUserId(userId)
      .stream()
      .map(
        c ->
          new ClaimDTO(
            c.id, c.userId, c.state, c.reserve, c.type, c.audioURL, c.registrationDate,
            c.claimSource))
      .collect(Collectors.toList());
  }

  @RequestMapping(path = "/activeClaims/{userId}", method = RequestMethod.GET)
  public ActiveClaimsDTO getActiveClaims(@PathVariable String userId) {
    log.info("Getting active claim status for member: {}", userId);

    Long activeClaims = claimsRepository.findByUserId(userId).stream()
      .filter(c -> Objects.equals(c.state, OPEN.name()))
      .count();

    return new ActiveClaimsDTO(activeClaims.intValue());
  }

  @RequestMapping(path = "/stat", method = RequestMethod.GET)
  public Map<String, Long> getClaimsStatisticsByState() {
    Map<String, Long> statistics = new HashMap<>();
    for (ClaimsAggregate.ClaimStates state : ClaimsAggregate.ClaimStates.values()) {
      statistics.put(state.name(), claimsRepository.countByState(state));
    }

    return statistics;
  }

  @RequestMapping(path = "/claim", method = RequestMethod.GET)
  public ResponseEntity<ClaimDTO> getClaim(@RequestParam String claimID) {
    log.info("Getting claim with ID:" + claimID);
    ClaimEntity claim = claimsRepository.findById(claimID)
      .orElseThrow(() -> new ResourceNotFoundException("Could not find claim with id:" + claimID));
    ClaimDTO cdto = new ClaimDTO(claim);
    return ResponseEntity.ok(cdto);
  }

  @RequestMapping(path = "/adddataitem", method = RequestMethod.POST)
  public ResponseEntity<?> addDataItem(@RequestBody DataItemDTO data) {
    log.info("Adding data item:" + data.toString());
    UUID uid = UUID.randomUUID();
    AddDataItemCommand command = new AddDataItemCommand(uid.toString(), data.claimID,
      LocalDateTime.now(), data.userId,
      data.type, data.name, data.title, data.received, data.value);
    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/addnote", method = RequestMethod.POST)
  public ResponseEntity<?> addNote(@RequestBody NoteDTO note) {
    log.info("Adding claim note:" + note.toString());
    UUID uid = UUID.randomUUID();
    AddNoteCommand command = new AddNoteCommand(uid.toString(), note.claimID, LocalDateTime.now(),
      note.text,
      note.userId, note.fileURL);
    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/addpayment", method = RequestMethod.POST)
  public ResponseEntity<?> addPayment(@RequestBody PaymentDTO payment) {
    log.info("Adding manual payment note:" + payment.toString());
    UUID uid = UUID.randomUUID();
    AddPaymentCommand command = new AddPaymentCommand(uid.toString(), payment.claimID,
      LocalDateTime.now(),
      payment.userId, payment.amount, payment.deductible, payment.note, payment.payoutDate, payment.exGratia,
      payment.handlerReference);

    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/{memberId}/addAutomaticPayment", method = RequestMethod.POST)
  public ResponseEntity<?> addAutomaticPayment(@PathVariable(name = "memberId") String memberId,
    @RequestBody PaymentRequestDTO request) {
    log.debug("add automatic payment: {}" + request.toString());

    Optional<Member> memberOptional = memberService.getMember(memberId);

    if (!memberOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    val member = memberOptional.get();

    SanctionStatus memberStatus = meerkat
      .getMemberSanctionStatus(String.format("%s %s", member.getFirstName(), member.getLastName()));

    if (memberStatus.equals(SanctionStatus.FullHit)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    if (!request.isSanctionCheckSkipped()
      && (memberStatus.equals(SanctionStatus.Undetermined)
      || memberStatus.equals(SanctionStatus.PartialHit))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    if (request.isSanctionCheckSkipped()) {
      Optional<ClaimEntity> claimOptional = claimsRepository
        .findById(request.getClaimId().toString());

      if (!claimOptional.isPresent()) {
        return ResponseEntity.notFound().build();
      }

      if (request.getPaymentRequestNote() == null
        || request.getPaymentRequestNote().trim().length() < 5) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

      ClaimEntity claim = claimOptional.get();

      UUID uid = UUID.randomUUID();
      AddNoteCommand command =
        new AddNoteCommand(
          uid.toString(),
          request.getClaimId().toString(),
          LocalDateTime.now(),
          request.getPaymentRequestNote(),
          memberId,
          claim.audioURL);

      commandBus.sendAndWait(command);
    }

    AddAutomaticPaymentCommand addAutomaticPaymentCommand =
      new AddAutomaticPaymentCommand(
        request.getClaimId().toString(),
        memberId,
        request.getAmount(),
        request.getDeductible(),
        request.getPaymentRequestNote(),
        request.isExGratia(),
        request.getHandlerReference(),
        request.isSanctionCheckSkipped());

    commandBus.sendAndWait(addAutomaticPaymentCommand);

    return ResponseEntity.accepted().build();
  }

  @RequestMapping(path = "/updatereserve", method = RequestMethod.POST)
  public ResponseEntity<?> updateReserve(@RequestBody ReserveDTO reserve) {
    log.info("Updating claim reserve: " + reserve.toString());

    UpdateClaimsReserveCommand command =
        new UpdateClaimsReserveCommand(
            reserve.claimID, reserve.userId, LocalDateTime.now(), reserve.amount);

    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/updatestate", method = RequestMethod.POST)
  public ResponseEntity<?> updateState(@RequestBody ClaimStateDTO state) {
    log.info("Updating claim reserve: " + state.toString());

    UpdateClaimsStateCommand command = new UpdateClaimsStateCommand(state.claimID, state.userId,
      LocalDateTime.now(),
      state.state);

    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/updatetype", method = RequestMethod.POST)
  public ResponseEntity<?> updateType(@RequestBody ClaimTypeDTO type) {
    log.info("Updating claim reserve: " + type.toString());

    UpdateClaimTypeCommand command = new UpdateClaimTypeCommand(type.claimID, type.userId,
      LocalDateTime.now(),
      type.type);

    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "claimTypes", method = RequestMethod.GET)
  public ResponseEntity<ArrayList<ClaimType>> claimTypes() {

    ArrayList<ClaimType> claimTypes = new ArrayList<>();

    ClaimDataType typeDate = new ClaimDataType(DataType.DATE, "DATE", "Date");
    ClaimDataType typePlace = new ClaimDataType(DataType.TEXT, "PLACE", "Place");
    ClaimDataType typeItem = new ClaimDataType(DataType.ASSET, "ITEM", "Item");
    ClaimDataType typePoliceReport = new ClaimDataType(DataType.FILE, "POLICE_REPORT",
      "Police report");
    ClaimDataType typeReceipt = new ClaimDataType(DataType.FILE, "RECEIPT", "Receipt");
    ClaimDataType typeTicket = new ClaimDataType(DataType.TICKET, "TICKET", "Ticket");

    ClaimType ct1 = new ClaimType("Theft - Other", "Theft - Other", false);
    ct1.addRequiredData(typeDate);
    ct1.addRequiredData(typePlace);
    ct1.addRequiredData(typeItem);
    ct1.addOptionalData(typePoliceReport);
    ct1.addOptionalData(typeReceipt);

    ClaimType ct2 = new ClaimType("Theft - Home", "Theft - Home", false);
    ct2.addRequiredData(typeDate);
    ct2.addRequiredData(typePlace);
    ct2.addRequiredData(typeItem);
    ct2.addOptionalData(typePoliceReport);
    ct2.addOptionalData(typeReceipt);

    ClaimType ct3 = new ClaimType("Theft - Bike", "Theft - Bike", false);
    ct3.addRequiredData(typeDate);
    ct3.addRequiredData(typePlace);
    ct3.addRequiredData(typeItem);
    ct3.addOptionalData(typePoliceReport);
    ct3.addOptionalData(typeReceipt);

    ClaimType ct4 = new ClaimType("Assault", "Assault", false);
    ct4.addRequiredData(typeDate);
    ct4.addRequiredData(typePlace);
    ct4.addOptionalData(typePoliceReport);

    ClaimType ct5 = new ClaimType("Drulle - Mobile", "Drulle - Mobile", false);
    ct5.addRequiredData(typeDate);
    ct5.addRequiredData(typePlace);
    ct5.addRequiredData(typeItem);
    ct5.addOptionalData(typePoliceReport);
    ct5.addOptionalData(typeReceipt);

    ClaimType ct6 = new ClaimType("Drulle - Other", "Drulle - Other", false);
    ct6.addRequiredData(typeDate);
    ct6.addRequiredData(typePlace);
    ct6.addRequiredData(typeItem);
    ct6.addOptionalData(typePoliceReport);
    ct6.addOptionalData(typeReceipt);

    ClaimType ct7 = new ClaimType("Water Damage - Kitchen", "Water Damage - Kitchen", false);
    ct7.addRequiredData(typeDate);

    ClaimType ct8 = new ClaimType("Water Damage - Bathroom", "Water Damage - Bathroom", false);
    ct8.addRequiredData(typeDate);

    ClaimType ct9 = new ClaimType("Travel - Accident and Health", "Travel - Accident and Health",
      false);
    ct9.addRequiredData(typeDate);
    ct9.addRequiredData(typePlace);
    ct9.addOptionalData(typePoliceReport);
    ct9.addOptionalData(typeReceipt);

    ClaimType ct10 = new ClaimType("Travel - Delayed Luggage", "Travel - Delayed Luggage", false);
    ct10.addRequiredData(typeDate);
    ct10.addRequiredData(typePlace);
    ct10.addOptionalData(typeTicket);

    ClaimType ct11 = new ClaimType("Not covered", "Not covered", false);
    ct11.addRequiredData(typeDate);

    claimTypes.add(ct1);
    claimTypes.add(ct2);
    claimTypes.add(ct3);
    claimTypes.add(ct4);
    claimTypes.add(ct5);

    claimTypes.add(ct6);
    claimTypes.add(ct7);
    claimTypes.add(ct8);
    claimTypes.add(ct9);
    claimTypes.add(ct10);
    claimTypes.add(ct11);

    // old claim types
    ClaimDataType c11 = new ClaimDataType(DataType.DATE, "DATE", "Datum");
    ClaimDataType c12 = new ClaimDataType(DataType.TEXT, "PLACE", "Plats");
    ClaimDataType c13 = new ClaimDataType(DataType.ASSET, "ITEM", "Pryl");
    ClaimDataType c14 = new ClaimDataType(DataType.FILE, "POLICE_REPORT", "Polisanmälan");
    ClaimDataType c15 = new ClaimDataType(DataType.FILE, "RECIEPT", "Kvitto");

    ClaimType oldCt1 = new ClaimType("THEFT", "Stöld", true);
    oldCt1.addRequiredData(c11);
    oldCt1.addRequiredData(c12);
    oldCt1.addRequiredData(c13);
    oldCt1.addOptionalData(c14);
    oldCt1.addOptionalData(c15);

    ClaimType oldCt2 = new ClaimType("FIRE", "Brand", true);
    oldCt2.addRequiredData(c11);
    oldCt2.addRequiredData(c12);
    oldCt2.addRequiredData(c13);
    oldCt2.addOptionalData(c14);
    oldCt2.addOptionalData(c15);

    ClaimType oldCt3 = new ClaimType("DRULLE", "Drulle", true);
    oldCt3.addRequiredData(c11);
    oldCt3.addRequiredData(c12);
    oldCt3.addRequiredData(c13);
    oldCt3.addOptionalData(c14);
    oldCt3.addOptionalData(c15);

    ClaimType oldCt4 = new ClaimType("FILE", "Brand", true);
    oldCt4.addRequiredData(c11);
    oldCt4.addRequiredData(c12);
    oldCt4.addRequiredData(c13);
    oldCt4.addOptionalData(c14);
    oldCt4.addOptionalData(c15);

    claimTypes.add(oldCt1);
    claimTypes.add(oldCt2);
    claimTypes.add(oldCt3);
    claimTypes.add(oldCt4);

    return ResponseEntity.ok(claimTypes);
  }

  @PostMapping("/many")
  public ResponseEntity<Stream<ClaimDTO>> getClaimsByIds(@RequestBody ClaimsByIdsDTO dto) {
    val claims = claimsRepository
      .findAllById(dto.getIds().stream().map(id -> id.toString()).collect(Collectors.toList()));

    if (claims.size() != dto.getIds().size()) {
      log.error("Length mismatch on supplied claims and found claims: wanted {}, found {}",
        dto.getIds().size(), claims.size());
      return ResponseEntity.notFound().build();
}

    return ResponseEntity.ok(claims.stream().map(claim -> new ClaimDTO(claim)));
  }
}
