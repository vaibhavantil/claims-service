package com.hedvig.claims.web;

import static com.hedvig.claims.aggregates.ClaimsAggregate.ClaimStates.OPEN;

import com.hedvig.claims.aggregates.ClaimsAggregate;
import com.hedvig.claims.commands.AddDataItemCommand;
import com.hedvig.claims.commands.AddNoteCommand;
import com.hedvig.claims.commands.AddPaymentCommand;
import com.hedvig.claims.commands.CreateClaimCommand;
import com.hedvig.claims.commands.ExecutePaymentCommand;
import com.hedvig.claims.commands.UpdateClaimTypeCommand;
import com.hedvig.claims.commands.UpdateClaimsReserveCommand;
import com.hedvig.claims.commands.UpdateClaimsStateCommand;
import com.hedvig.claims.query.ClaimEntity;
import com.hedvig.claims.query.ClaimsRepository;
import com.hedvig.claims.query.ResourceNotFoundException;
import com.hedvig.claims.web.dto.ActiveClaimsDTO;
import com.hedvig.claims.web.dto.ClaimDTO;
import com.hedvig.claims.web.dto.ClaimDataType;
import com.hedvig.claims.web.dto.ClaimDataType.DataType;
import com.hedvig.claims.web.dto.ClaimStateDTO;
import com.hedvig.claims.web.dto.ClaimType;
import com.hedvig.claims.web.dto.ClaimTypeDTO;
import com.hedvig.claims.web.dto.DataItemDTO;
import com.hedvig.claims.web.dto.NoteDTO;
import com.hedvig.claims.web.dto.PaymentDTO;
import com.hedvig.claims.web.dto.ReserveDTO;
import com.hedvig.claims.web.dto.StartClaimAudioDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

  @Autowired
  public InternalController(CommandBus commandBus, ClaimsRepository repository) {
    this.commandBus = new DefaultCommandGateway(commandBus);
    this.claimsRepository = repository;
  }

  @RequestMapping(path = "/startClaimFromAudio", method = RequestMethod.POST)
  public ResponseEntity<?> initiateClaim(@RequestBody StartClaimAudioDTO requestData) {
    log.info("Claim recieved!:" + requestData.toString());
    UUID uid = UUID.randomUUID();
    commandBus.sendAndWait(
        new CreateClaimCommand(
            uid.toString(),
            requestData.getUserId(),
            LocalDateTime.now(),
            requestData.getAudioURL()));
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/listclaims", method = RequestMethod.GET)
  public ResponseEntity<List<ClaimDTO>> getClaimsList() {
    log.info("Getting all claims:");
    ArrayList<ClaimDTO> claims = new ArrayList<>();
    for (ClaimEntity c : claimsRepository.findAll()) {
      claims.add(
          new ClaimDTO(c.id, c.userId, c.state, c.reserve, c.type, c.audioURL, c.registrationDate));
    }

    return ResponseEntity.ok(claims);
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
                    c.id, c.userId, c.state, c.reserve, c.type, c.audioURL, c.registrationDate))
        .collect(Collectors.toList());
  }

  @RequestMapping(path = "/activeClaims/{userId}", method = RequestMethod.GET)
  public ActiveClaimsDTO getActiveClaims(@PathVariable String userId) {
    log.info("Getting active claim status for member: {}", userId);

    Long activeClaims =
        claimsRepository
            .findByUserId(userId)
            .stream()
            .filter(c -> Objects.equals(c.state, OPEN.name()))
            .count();

    return new ActiveClaimsDTO(activeClaims.intValue());
  }

  @RequestMapping(path = "/stat", method = RequestMethod.GET)
  public Map<String, Long> getClaimsStatisticsByState() {
    Map<String, Long> statistics = new HashMap<>();
    for (ClaimsAggregate.ClaimStates state : ClaimsAggregate.ClaimStates.values()) {
      statistics.put(state.name(), claimsRepository.countByState(state.name()));
    }

    return statistics;
  }

  @RequestMapping(path = "/claim", method = RequestMethod.GET)
  public ResponseEntity<ClaimDTO> getClaim(@RequestParam String claimID) {
    log.info("Getting claim with ID:" + claimID);
    ClaimEntity claim =
        claimsRepository
            .findById(claimID)
            .orElseThrow(
                () -> new ResourceNotFoundException("Could not find claim with id:" + claimID));
    ClaimDTO cdto = new ClaimDTO(claim);
    return ResponseEntity.ok(cdto);
  }

  @RequestMapping(path = "/adddataitem", method = RequestMethod.POST)
  public ResponseEntity<?> addDataItem(@RequestBody DataItemDTO data) {
    log.info("Adding data item:" + data.toString());
    UUID uid = UUID.randomUUID();
    AddDataItemCommand command =
        new AddDataItemCommand(
            uid.toString(),
            data.claimID,
            LocalDateTime.now(),
            data.userId,
            data.type,
            data.name,
            data.title,
            data.received,
            data.value);
    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/addnote", method = RequestMethod.POST)
  public ResponseEntity<?> addNote(@RequestBody NoteDTO note) {
    log.info("Adding claim note:" + note.toString());
    UUID uid = UUID.randomUUID();
    AddNoteCommand command =
        new AddNoteCommand(
            uid.toString(),
            note.claimID,
            LocalDateTime.now(),
            note.text,
            note.userId,
            note.fileURL);
    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/addpayment", method = RequestMethod.POST)
  public ResponseEntity<?> addPayment(@RequestBody PaymentDTO payment) {
    log.info("Adding manual payment note:" + payment.toString());
    UUID uid = UUID.randomUUID();
    AddPaymentCommand command =
        new AddPaymentCommand(
            uid.toString(),
            payment.claimID,
            LocalDateTime.now(),
            payment.userId,
            payment.amount,
            payment.note,
            payment.payoutDate,
            payment.exGratia,
            payment.type,
            payment.handlerReference);

    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/{memberId}/executePayment", method = RequestMethod.POST)
  public ResponseEntity<?> executePayment(@PathVariable(name = "memberId") String memberId,
      @RequestBody PaymentDTO payment) {
    log.info("Execute automatic payment note:" + payment.toString());

    ExecutePaymentCommand executePaymentCommand =
        new ExecutePaymentCommand(
            UUID.randomUUID().toString(),
            payment.claimID,
            memberId,
            Money.of(payment.amount, "SEK"),
            payment.note,
            payment.exGratia,
            payment.handlerReference);

    commandBus.sendAndWait(executePaymentCommand);

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

    UpdateClaimsStateCommand command =
        new UpdateClaimsStateCommand(state.claimID, state.userId, LocalDateTime.now(), state.state);

    commandBus.sendAndWait(command);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping(path = "/updatetype", method = RequestMethod.POST)
  public ResponseEntity<?> updateType(@RequestBody ClaimTypeDTO type) {
    log.info("Updating claim reserve: " + type.toString());

    UpdateClaimTypeCommand command =
        new UpdateClaimTypeCommand(type.claimID, type.userId, LocalDateTime.now(), type.type);

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

    ClaimType ct1 = new ClaimType("Theft - Other", "Theft - Other");
    ct1.addRequiredData(typeDate);
    ct1.addRequiredData(typePlace);
    ct1.addRequiredData(typeItem);
    ct1.addOptionalData(typePoliceReport);
    ct1.addOptionalData(typeReceipt);

    ClaimType ct2 = new ClaimType("Theft - Home", "Theft - Home");
    ct2.addRequiredData(typeDate);
    ct2.addRequiredData(typePlace);
    ct2.addRequiredData(typeItem);
    ct2.addOptionalData(typePoliceReport);
    ct2.addOptionalData(typeReceipt);

    ClaimType ct3 = new ClaimType("Theft - Bike", "Theft - Bike");
    ct3.addRequiredData(typeDate);
    ct3.addRequiredData(typePlace);
    ct3.addRequiredData(typeItem);
    ct3.addOptionalData(typePoliceReport);
    ct3.addOptionalData(typeReceipt);

    ClaimType ct4 = new ClaimType("Assault", "Assault");
    ct4.addRequiredData(typeDate);
    ct4.addRequiredData(typePlace);
    ct4.addOptionalData(typePoliceReport);

    ClaimType ct5 = new ClaimType("Drulle - Mobile", "Drulle - Mobile");
    ct5.addRequiredData(typeDate);
    ct5.addRequiredData(typePlace);
    ct5.addRequiredData(typeItem);
    ct5.addOptionalData(typePoliceReport);
    ct5.addOptionalData(typeReceipt);

    ClaimType ct6 = new ClaimType("Drulle - Other", "Drulle - Other");
    ct6.addRequiredData(typeDate);
    ct6.addRequiredData(typePlace);
    ct6.addRequiredData(typeItem);
    ct6.addOptionalData(typePoliceReport);
    ct6.addOptionalData(typeReceipt);

    ClaimType ct7 = new ClaimType("Water Damage - Kitchen", "Water Damage - Kitchen");
    ct7.addRequiredData(typeDate);

    ClaimType ct8 = new ClaimType("Water Damage - Bathroom", "Water Damage - Bathroom");
    ct8.addRequiredData(typeDate);

    ClaimType ct9 = new ClaimType("Travel - Accident and Health", "Travel - Accident and Health");
    ct9.addRequiredData(typeDate);
    ct9.addRequiredData(typePlace);
    ct9.addOptionalData(typePoliceReport);
    ct9.addOptionalData(typeReceipt);

    ClaimType ct10 = new ClaimType("Travel - Delayed Luggage", "Travel - Delayed Luggage");
    ct10.addRequiredData(typeDate);
    ct10.addRequiredData(typePlace);
    ct10.addOptionalData(typeTicket);

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

    return ResponseEntity.ok(claimTypes);
  }
}
